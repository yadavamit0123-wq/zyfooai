package com.pt.zyfooai.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.pt.zyfooai.AppConfig;
import com.pt.zyfooai.utils.AppUpdateHelper;
import com.pt.zyfooai.utils.RemoteConfigHelper;
import com.pt.zyfooai.MyApplication;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.SubscriptionModel;
import com.pt.zyfooai.model.UserItem;
import com.pt.zyfooai.ui.Functions;
import com.pt.zyfooai.ui.dialog.UniversalDialog;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.MyUtils;
import com.pt.zyfooai.utils.NetworkConnectivity;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.Util;
import com.pt.zyfooai.viewmodel.HomeViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SuppressLint("CustomSplashScreen")
public class CustomSplashActivity extends AppCompatActivity {

    PreferenceManager preferenceManager;
    UniversalDialog universalDialog;
    String status = "";
    NetworkConnectivity networkConnectivity;

    HomeViewModel homeViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashy);

        MyUtils.hideNavigation(this, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        preferenceManager = new PreferenceManager(this);
        universalDialog = new UniversalDialog(this, false);
        status = preferenceManager.getString(Constant.STATUS);

        networkConnectivity = new NetworkConnectivity(this);


        loadData();

    }


    public void loadData() {
        if (networkConnectivity.isConnected()) {
            RemoteConfigHelper.fetchAndActivate(this, () -> {
                AppConfig.API_KEY = preferenceManager.getString(Constant.API_KEY);
                loadAppData();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateHelper.checkForUpdate(this);
    }

    public void loadAppData() {

//        Toast.makeText(this, ""+preferenceManager.getString(Constant.USER_ID), Toast.LENGTH_SHORT).show();

        Log.d("API_KEY__", "loadAppData: "+preferenceManager.getString(Constant.API_KEY));

        homeViewModel.getAppInfo(preferenceManager.getString(Constant.USER_ID)).observe(this, appInfos -> {

            findViewById(R.id.lottieLogin).setVisibility(View.GONE);

            preferenceManager.setString(Constant.LANGUAGE_NAME, "All");

            if (appInfos != null) {

                MyUtils.showResponse(appInfos);

                if (appInfos.getAppInfo() != null) {
                    preferenceManager.setString(Constant.PRIVACY_POLICY, appInfos.getAppInfo().getPrivacyPolicy());
                    preferenceManager.setString(Constant.TERM_CONDITION, appInfos.getAppInfo().getTermsCondition());
                    preferenceManager.setString(Constant.REFUND_POLICY, appInfos.getAppInfo().getRefundPolicy());
                    preferenceManager.setString(Constant.PRIVACY_POLICY_LINK, appInfos.getAppInfo().getPrivacyPolicy());
                    preferenceManager.setString(Constant.ONESIGNAL_APP_ID, appInfos.getAppInfo().getOnesignalAppId());
                    preferenceManager.setString(Constant.RAZORPAY_KEY_ID, appInfos.getAppInfo().getRazorpayKeyId());
                    preferenceManager.setString(Constant.RAZORPAY_SECRET_KEY, appInfos.getAppInfo().getRazorpayKeySecret());
                    preferenceManager.setString(Constant.CONTACT_WHATSAPP, appInfos.getAppInfo().whatsappNumber);
                    preferenceManager.setString(Constant.CURRENCY, appInfos.getAppInfo().currency);
                }

                if (appInfos.getAdsModel() != null) {
                    preferenceManager.setString(Constant.PUBLISHER_ID, appInfos.getAdsModel().getAdmobAppId());
                    preferenceManager.setString(Constant.BANNER_AD_ID, appInfos.getAdsModel().getAdmobBanner());
                    preferenceManager.setString(Constant.INTERSTITIAL_AD_ID, appInfos.getAdsModel().getAdmobInter());
                    preferenceManager.setBoolean(Constant.ADS_ENABLE, appInfos.getAdsModel().getAdEnabled() == 1);
                    preferenceManager.setBoolean(Constant.INTERSTITIAL_AD_ENABLED, appInfos.getAdsModel().getInterstitialAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.BANNER_AD_ENABLED, appInfos.getAdsModel().getBannerAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.NATIVE_AD_ENABLED, appInfos.getAdsModel().getNativeAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.OPEN_APP_AD_ENABLED, appInfos.getAdsModel().getAppOpensAdsEnable() == 1);
                    preferenceManager.setBoolean(Constant.REWARD_AD_ENABLED, appInfos.getAdsModel().getRewardedAdsEnable() == 1);
                    preferenceManager.setInt(Constant.INTERSTITIAL_AD_CLICK, appInfos.getAdsModel().getAdmobInterCount());
                    preferenceManager.setString(Constant.NATIVE_AD_ID, appInfos.getAdsModel().getAdmobNative());
                    preferenceManager.setString(Constant.REWARD_AD, appInfos.getAdsModel().getAdmobReward());
                    preferenceManager.setString(Constant.OPEN_AD_ID, appInfos.getAdsModel().getAdmobAppOpen());
                }

                if (appInfos.getUserItem() != null){
                    UserItem userItem = appInfos.getUserItem();
                    preferenceManager.setBoolean(Constant.IS_LOGIN, true);
                    Functions.saveUserData(CustomSplashActivity.this,userItem);
                    boolean isSubscribed = isPlanActive(userItem.planEndDate);
                    preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, isSubscribed);

                    gotoMainActivity();

                }else {

                    if (!preferenceManager.getBoolean(Constant.IS_LOGIN)){
                        Intent intent = new Intent(CustomSplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(this, "User Data Null", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CustomSplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }else {
                Toast.makeText(this, "Data Null", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Helper function to check if the plan is active
    private boolean isPlanActive(String planEndDate) {
        try {
            // Parse the planEndDate string into a LocalDate
            DateTimeFormatter formatter = null; // Adjust format as per your date string
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate endDate = LocalDate.parse(planEndDate, formatter);
                return endDate.isAfter(LocalDate.now()) || endDate.isEqual(LocalDate.now());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // If there's an error parsing the date, assume the plan is not active
        }
        return false;
    }

    private void gotoMainActivity() {
        Intent intent;
        if (getIntent().getBooleanExtra(Constant.INTENT_IS_FROM_NOTIFICATION, false)) {
            intent = new Intent(CustomSplashActivity.this, MainActivity.class);
            intent.putExtra(Constant.INTENT_TYPE, preferenceManager.getString(Constant.PRF_TYPE));
            intent.putExtra(Constant.INTENT_FEST_ID, preferenceManager.getString(Constant.PRF_ID));
            intent.putExtra(Constant.INTENT_FEST_NAME, preferenceManager.getString(Constant.PRF_NAME));
            intent.putExtra(Constant.INTENT_POST_IMAGE, "");
            intent.putExtra(Constant.INTENT_VIDEO, false);
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(CustomSplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void storeDevice() {
        homeViewModel.storeDevice(MyUtils.getDeviceId(this)).observe(this, new Observer<SubscriptionModel>() {
            @Override
            public void onChanged(SubscriptionModel userResponse) {


                if (userResponse != null) {
                    preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, userResponse.is_subscribed);
                    gotoMainActivity();

                } else {
                    gotoMainActivity();
                }
            }
        });
    }
}