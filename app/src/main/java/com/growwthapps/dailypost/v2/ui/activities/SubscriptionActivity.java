package com.growwthapps.dailypost.v2.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.growwthapps.dailypost.v2.BuildConfig;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.ui.adapters.SubscriptionAdapter;
import com.growwthapps.dailypost.v2.databinding.ActivitySubscriptionBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.ui.dialog.UniversalDialog;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubscriptionActivity extends AppCompatActivity implements PaymentResultListener {

    ActivitySubscriptionBinding binding;
    private PreferenceManager preferenceManager;
    private UniversalDialog universalDialog;
    SubscriptionAdapter adapter;
    List<SubscriptionModel> plan_list = new ArrayList<>();
    String selected_id = "", selected_price = "", selected_name = "";
    Activity context;
    SubscriptionModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        int i;

        Window window = getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.clearFlags(67108864);
        window.setStatusBarColor(0);
        int i3 = Build.VERSION.SDK_INT;
        View decorView = window.getDecorView();
        if (i3 >= 26) {
            i = 1296;
        } else {
            i = 1280;
        }
        decorView.setSystemUiVisibility(i);
        if (!BuildConfig.DEBUG){
            getWindow().setFlags(8192, 8192);
        }

        universalDialog = new UniversalDialog(this, false);

        preferenceManager = new PreferenceManager(this);

        binding.backBtn.setOnClickListener(view -> onBackPressed());

        model = (SubscriptionModel) getIntent().getSerializableExtra("model");
        if (model != null) {
            selected_price = model.discount_price;
            selected_name = model.name;
            selected_id = model.id;
            manageRazorPay(Integer.parseInt(selected_price));
        }

        Constant.getHomeViewModel(this).getSubscriptionPlan().observe(this, new Observer<List<SubscriptionModel>>() {
            @Override
            public void onChanged(List<SubscriptionModel> subscriptionModels) {
                if (subscriptionModels != null && !subscriptionModels.isEmpty()){
                    plan_list = subscriptionModels;

                    adapter = new SubscriptionAdapter(context, plan_list, new AdapterClickListener() {
                        @Override
                        public void onItemClick(View view, int pos, Object object) {

                            SubscriptionModel model = (SubscriptionModel) object;
                            selected_price = model.discount_price;
                            selected_name = model.name;
                            selected_id = model.id;

                            //   Log.d("SubscriptionAdapter", "onItemClick: "+selected_price+ " "+selected_name+ " "+selected_id);

                        }
                    });


                    selected_price = plan_list.get(0).discount_price;

                    //  adapter.notifyDataSetChanged();
                    binding.shimmerLay.setVisibility(View.GONE);
                    binding.recycler.setVisibility(View.VISIBLE);
                    binding.recycler.setAdapter(adapter);
                    setupBuyBtn();
                }
            }
        });


    }

    private void setupBuyBtn() {
        binding.llSubscribeNow.setOnClickListener(view -> {
            manageRazorPay(Integer.parseInt(selected_price));
        });
    }

    private void manageRazorPay(int price) {
        try {

            String mobile = preferenceManager.getString(Constant.USER_PHONE);
            String email = preferenceManager.getString(Constant.USER_EMAIL);

            JSONObject object = new JSONObject();
            object.put("name", getString(R.string.app_name));
            object.put("description", price*100);
            object.put("theme.color", "#FF8E0B");

            object.put("currency", "INR");
            object.put("amount", price*100);
            if (mobile != null && !mobile.trim().isEmpty()) object.put("prefill.contact", mobile);

            if (email != null && !email.trim().isEmpty()) object.put("prefill.email", email);

            Checkout checkout = new Checkout();
            checkout.setImage(R.drawable.logo);

            checkout.setKeyID(preferenceManager.getString(Constant.RAZORPAY_KEY_ID));
            checkout.open(this, object);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        if (razorpayPaymentID != null && !razorpayPaymentID.isEmpty()) {

            capturePayment(razorpayPaymentID, Integer.parseInt(selected_price));
            createTransactions(razorpayPaymentID);
        } else {
           this.runOnUiThread(() -> {
                Toast.makeText(this, "Something want wrong", Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        if (response != null && !response.trim().isEmpty()) {
            try {
                this.runOnUiThread(() -> {
                    Toast.makeText(this, response.trim(), Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.runOnUiThread(() -> {
                Toast.makeText(this, "Access Denied", Toast.LENGTH_LONG).show();
            });
        }
        if (model != null) {
            finish();
        }
    }

    private void capturePayment(String paymentID, int price) {
        OkHttpClient client = new OkHttpClient();

        // Check for null or empty values
        if (paymentID == null || paymentID.isEmpty()) {
            Log.e("Capture Payment", "Payment ID is null or empty");
            return;
        }
        if (price == 0) {
            Log.e("Capture Payment", "Price is null or empty");
            return;
        }

        String keyId = preferenceManager.getString(Constant.RAZORPAY_KEY_ID);
        String secretKey = preferenceManager.getString(Constant.RAZORPAY_SECRET_KEY);

        if (keyId == null || secretKey == null || keyId.isEmpty() || secretKey.isEmpty()) {
            Log.e("Capture Payment", "Razorpay Key ID or Secret Key is null or empty");
            return;
        }

        String credentials = keyId + ":" + secretKey;
        String authHeader = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        RequestBody body = new FormBody.Builder()
                .add("amount", String.valueOf(price * 100)) // amount to be captured in currency subunits
                .build();

        Request request = new Request.Builder()
                .url("https://api.razorpay.com/v1/payments/" + paymentID + "/capture")
                .addHeader("Authorization", authHeader)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Capture Payment", "Request failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("Capture Payment", "Unexpected response: " + response);
                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.d("Capture Payment", "Payment Captured Successfully: " + response.body().string());
                }
            }
        });

        if (model != null) {
            finish();
        }
    }

    ProgressDialog progressDialog;
    private void createTransactions(String paymentId) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating..");
        progressDialog.show();
       Constant.getUserViewModel(this).storeTransaction(preferenceManager.getString(Constant.USER_ID), selected_id, selected_price, paymentId).observe(this, userItem -> {
           if (userItem != null) {
               progressDialog.dismiss();
               preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, true);
               preferenceManager.setString(Constant.PLAN_ID, userItem.planId);
               preferenceManager.setString(Constant.PLAN_NAME, userItem.planName);
               preferenceManager.setString(Constant.PLAN_END_DATE, userItem.planEndDate);

               universalDialog.showSuccessDialog("message", SubscriptionActivity.this.getString(R.string.ok));
               universalDialog.show();

               Toast.makeText(SubscriptionActivity.this, "Payment DONE Successfully!  ", Toast.LENGTH_SHORT).show();

               new Handler().postDelayed(() -> {
                   Intent intent = new Intent(SubscriptionActivity.this, MainActivity.class);
                   SubscriptionActivity.this.startActivity(intent);
                   SubscriptionActivity.this.finish();
               }, 2500);

           }

       });
    }
}