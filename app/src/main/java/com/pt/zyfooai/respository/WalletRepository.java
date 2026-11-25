package com.pt.zyfooai.respository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pt.zyfooai.api.ApiClient;
import com.pt.zyfooai.api.ApiService;
import com.pt.zyfooai.model.ReferralListItem;
import com.pt.zyfooai.model.ReferralListResponse;
import com.pt.zyfooai.model.ReferralMeResponse;
import com.pt.zyfooai.model.WalletHistoryItem;
import com.pt.zyfooai.model.WalletHistoryResponse;
import com.pt.zyfooai.model.WalletPayRequest;
import com.pt.zyfooai.model.WalletPayResponse;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.WalletLedgerPrefs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class WalletRepository {

    private static final String TAG = "WalletRepository";
    private final ApiService api = ApiClient.getApiDataService();
    private final ReferralRepository referralRepository = new ReferralRepository();

    public LiveData<ReferralMeResponse> refreshBalance(String userId) {
        return referralRepository.getMe(userId);
    }

    public LiveData<WalletHistoryResponse> getHistory(Context context, String userId) {
        MutableLiveData<WalletHistoryResponse> live = new MutableLiveData<>();
        api.getWalletHistory(userId).enqueue(new Callback<WalletHistoryResponse>() {
            @Override
            public void onResponse(Call<WalletHistoryResponse> call, Response<WalletHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().data != null && !response.body().data.isEmpty()) {
                    WalletHistoryResponse body = response.body();
                    body.data = WalletLedgerPrefs.mergeLocalDebits(context, body.data);
                    if (body.walletBalance != null && context != null) {
                        new PreferenceManager(context)
                                .setString(Constant.REFER_WALLET_BALANCE, body.walletBalance);
                    }
                    live.setValue(body);
                    return;
                }
                // Fallback: build credits from referral list + local debits
                buildFallbackHistory(context, userId, live);
            }

            @Override
            public void onFailure(Call<WalletHistoryResponse> call, Throwable t) {
                Log.d(TAG, "getHistory: " + t.getMessage());
                buildFallbackHistory(context, userId, live);
            }
        });
        return live;
    }

    private void buildFallbackHistory(
            Context context,
            String userId,
            MutableLiveData<WalletHistoryResponse> live
    ) {
        api.getReferralList(userId, 1).enqueue(new Callback<ReferralListResponse>() {
            @Override
            public void onResponse(Call<ReferralListResponse> call, Response<ReferralListResponse> response) {
                WalletHistoryResponse out = new WalletHistoryResponse();
                out.successRaw = true;
                List<WalletHistoryItem> items = new ArrayList<>();
                String defaultAmount = "";
                if (context != null) {
                    defaultAmount = new PreferenceManager(context)
                            .getString(Constant.REFER_REFERRER_AMOUNT);
                    out.walletBalance = new PreferenceManager(context)
                            .getString(Constant.REFER_WALLET_BALANCE);
                }
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    for (ReferralListItem ref : response.body().data) {
                        if (ref == null) {
                            continue;
                        }
                        WalletHistoryItem row = new WalletHistoryItem();
                        row.type = "credit";
                        row.fromUser = ref.name;
                        row.createdAt = ref.date;
                        row.amount = (ref.amount != null && !ref.amount.isEmpty())
                                ? ref.amount
                                : defaultAmount;
                        row.title = "Referral earning";
                        items.add(row);
                    }
                }
                items = WalletLedgerPrefs.mergeLocalDebits(context, items);
                out.data = items;
                live.setValue(out);
            }

            @Override
            public void onFailure(Call<ReferralListResponse> call, Throwable t) {
                WalletHistoryResponse out = new WalletHistoryResponse();
                out.successRaw = true;
                out.data = WalletLedgerPrefs.mergeLocalDebits(context, new ArrayList<>());
                if (context != null) {
                    out.walletBalance = new PreferenceManager(context)
                            .getString(Constant.REFER_WALLET_BALANCE);
                }
                live.setValue(out);
            }
        });
    }

    public LiveData<WalletPayResponse> payWithWallet(String userId, String planId, String amount) {
        MutableLiveData<WalletPayResponse> live = new MutableLiveData<>();
        api.payWithWallet(new WalletPayRequest(userId, planId, amount))
                .enqueue(new Callback<WalletPayResponse>() {
                    @Override
                    public void onResponse(Call<WalletPayResponse> call, Response<WalletPayResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            live.setValue(response.body().resolved());
                        } else {
                            WalletPayResponse err = new WalletPayResponse();
                            err.successRaw = false;
                            err.message = "Pay failed (" + response.code() + ")";
                            live.setValue(err);
                        }
                    }

                    @Override
                    public void onFailure(Call<WalletPayResponse> call, Throwable t) {
                        Log.d(TAG, "payWithWallet: " + t.getMessage());
                        WalletPayResponse err = new WalletPayResponse();
                        err.successRaw = false;
                        err.message = t.getMessage() != null ? t.getMessage() : "Network error";
                        live.setValue(err);
                    }
                });
        return live;
    }
}
