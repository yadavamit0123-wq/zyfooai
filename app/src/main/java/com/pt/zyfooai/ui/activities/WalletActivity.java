package com.pt.zyfooai.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.SubscriptionModel;
import com.pt.zyfooai.model.WalletHistoryItem;
import com.pt.zyfooai.model.WalletPayResponse;
import com.pt.zyfooai.respository.WalletRepository;
import com.pt.zyfooai.ui.adapters.WalletHistoryAdapter;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.WalletLedgerPrefs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WalletActivity extends AppCompatActivity {

    private final WalletRepository repository = new WalletRepository();
    private PreferenceManager preferenceManager;
    private WalletHistoryAdapter historyAdapter;

    private TextView tvBalance;
    private View emptyHistory;
    private View errorHistory;
    private ProgressBar progressHistory;
    private RecyclerView rvHistory;

    private String userId = "";
    private double balanceValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        preferenceManager = new PreferenceManager(this);
        userId = preferenceManager.getString(Constant.USER_ID);
        if (userId == null) {
            userId = "";
        }

        tvBalance = findViewById(R.id.tvWalletBalance);
        emptyHistory = findViewById(R.id.emptyHistory);
        errorHistory = findViewById(R.id.errorHistory);
        progressHistory = findViewById(R.id.progressHistory);
        rvHistory = findViewById(R.id.rvWalletHistory);

        historyAdapter = new WalletHistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        findViewById(R.id.backImg).setOnClickListener(v -> finish());
        findViewById(R.id.btnRetryHistory).setOnClickListener(v -> loadHistory());
        findViewById(R.id.btnUseForSubscription).setOnClickListener(v -> showPlansDialog());

        bindCachedBalance();
        refreshBalance();
        loadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindCachedBalance();
    }

    private void bindCachedBalance() {
        String bal = preferenceManager.getString(Constant.REFER_WALLET_BALANCE);
        if (bal == null || bal.isEmpty()) {
            bal = "0";
        }
        balanceValue = parseAmount(bal);
        tvBalance.setText(getString(R.string.wallet_balance_amount, bal));
    }

    private void refreshBalance() {
        if (userId.isEmpty()) {
            return;
        }
        repository.refreshBalance(userId).observe(this, me -> {
            if (me != null && me.walletBalance != null) {
                preferenceManager.setString(Constant.REFER_WALLET_BALANCE, me.walletBalance);
                bindCachedBalance();
            }
        });
    }

    private void loadHistory() {
        progressHistory.setVisibility(View.VISIBLE);
        emptyHistory.setVisibility(View.GONE);
        errorHistory.setVisibility(View.GONE);
        rvHistory.setVisibility(View.GONE);

        repository.getHistory(this, userId).observe(this, response -> {
            progressHistory.setVisibility(View.GONE);
            if (response == null) {
                errorHistory.setVisibility(View.VISIBLE);
                return;
            }
            if (response.walletBalance != null && !response.walletBalance.isEmpty()) {
                preferenceManager.setString(Constant.REFER_WALLET_BALANCE, response.walletBalance);
                bindCachedBalance();
            }
            List<WalletHistoryItem> data = response.data;
            if (data == null || data.isEmpty()) {
                emptyHistory.setVisibility(View.VISIBLE);
                historyAdapter.submit(null);
                return;
            }
            rvHistory.setVisibility(View.VISIBLE);
            historyAdapter.submit(data);
        });
    }

    private void showPlansDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_wallet_plans);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }

        TextView hint = dialog.findViewById(R.id.tvPlansWalletHint);
        ProgressBar progress = dialog.findViewById(R.id.progressPlans);
        RecyclerView rvPlans = dialog.findViewById(R.id.rvWalletPlans);
        hint.setText(getString(R.string.referral_wallet_label,
                preferenceManager.getString(Constant.REFER_WALLET_BALANCE)));

        rvPlans.setLayoutManager(new LinearLayoutManager(this));
        progress.setVisibility(View.VISIBLE);

        Constant.getHomeViewModel(this).getSubscriptionPlan().observe(this, plans -> {
            progress.setVisibility(View.GONE);
            if (plans == null || plans.isEmpty()) {
                Toast.makeText(this, R.string.wallet_pay_failed, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            rvPlans.setAdapter(new PlanPickAdapter(plans, plan -> {
                dialog.dismiss();
                confirmAndPay(plan);
            }));
        });

        dialog.show();
    }

    private void confirmAndPay(SubscriptionModel plan) {
        if (plan == null) {
            return;
        }
        String priceStr = plan.discount_price != null && !plan.discount_price.isEmpty()
                ? plan.discount_price
                : plan.price;
        double price = parseAmount(priceStr);
        if (price <= 0) {
            Toast.makeText(this, R.string.wallet_pay_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        if (balanceValue + 0.001 < price) {
            Toast.makeText(this, R.string.wallet_insufficient, Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.wallet_paying));
        pd.setCancelable(false);
        pd.show();

        repository.payWithWallet(userId, plan.id, priceStr).observe(this, pay -> {
            if (pay != null && pay.isSuccess()) {
                String txnId = pay.transactionId != null && !pay.transactionId.isEmpty()
                        ? pay.transactionId
                        : ("WALLET-" + System.currentTimeMillis());
                if (pay.walletBalance != null) {
                    preferenceManager.setString(Constant.REFER_WALLET_BALANCE, pay.walletBalance);
                    bindCachedBalance();
                } else {
                    double next = Math.max(0, balanceValue - price);
                    preferenceManager.setString(Constant.REFER_WALLET_BALANCE,
                            String.valueOf((long) next));
                    bindCachedBalance();
                }
                recordLocalDebit(txnId, priceStr, plan.name);
                finalizeSubscription(pd, plan, txnId, priceStr);
            } else {
                pd.dismiss();
                String msg = pay != null && pay.message != null
                        ? pay.message
                        : getString(R.string.wallet_pay_failed);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recordLocalDebit(String txnId, String amount, String planName) {
        WalletHistoryItem debit = new WalletHistoryItem();
        debit.id = txnId;
        debit.type = "debit";
        debit.amount = amount;
        debit.planName = planName != null ? planName : "Subscription";
        debit.title = "Subscription";
        debit.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
        WalletLedgerPrefs.addDebit(this, debit);
        loadHistory();
    }

    private void finalizeSubscription(
            ProgressDialog pd,
            SubscriptionModel plan,
            String txnId,
            String amount
    ) {
        Constant.getUserViewModel(this)
                .storeTransaction(userId, plan.id, amount, txnId)
                .observe(this, userItem -> {
                    pd.dismiss();
                    if (userItem != null) {
                        preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, true);
                        if (userItem.planId != null) {
                            preferenceManager.setString(Constant.PLAN_ID, userItem.planId);
                        }
                        if (userItem.planName != null) {
                            preferenceManager.setString(Constant.PLAN_NAME, userItem.planName);
                        }
                        if (userItem.planEndDate != null) {
                            preferenceManager.setString(Constant.PLAN_END_DATE, userItem.planEndDate);
                        }
                        Toast.makeText(this, R.string.wallet_pay_success, Toast.LENGTH_SHORT).show();
                        refreshBalance();
                    } else {
                        Toast.makeText(this, R.string.wallet_pay_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static double parseAmount(String raw) {
        if (raw == null) {
            return 0;
        }
        try {
            String cleaned = raw.replace("₹", "").replace(",", "").trim();
            if (cleaned.isEmpty()) {
                return 0;
            }
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    private interface PlanClick {
        void onPay(SubscriptionModel plan);
    }

    private static class PlanPickAdapter extends RecyclerView.Adapter<PlanPickAdapter.Holder> {
        private final List<SubscriptionModel> plans;
        private final PlanClick click;

        PlanPickAdapter(List<SubscriptionModel> plans, PlanClick click) {
            this.plans = plans;
            this.click = click;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_wallet_plan, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            SubscriptionModel plan = plans.get(position);
            holder.name.setText(plan.name != null ? plan.name : "");
            String price = plan.discount_price != null && !plan.discount_price.isEmpty()
                    ? plan.discount_price
                    : (plan.price != null ? plan.price : "0");
            holder.price.setText(holder.itemView.getContext()
                    .getString(R.string.wallet_plan_price, price));
            holder.pay.setOnClickListener(v -> {
                if (click != null) {
                    click.onPay(plan);
                }
            });
        }

        @Override
        public int getItemCount() {
            return plans != null ? plans.size() : 0;
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView name;
            final TextView price;
            final TextView pay;

            Holder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tvPlanName);
                price = itemView.findViewById(R.id.tvPlanPrice);
                pay = itemView.findViewById(R.id.btnPayPlan);
            }
        }
    }
}
