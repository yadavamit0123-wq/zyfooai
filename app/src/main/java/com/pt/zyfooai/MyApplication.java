package com.pt.zyfooai;

import static com.pt.zyfooai.utils.Constant.IS_SUBSCRIBE;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.pt.zyfooai.AdsUtils.AppOpenManager;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.NetworkConnectivity;
import com.pt.zyfooai.utils.LocaleHelper;
import com.pt.zyfooai.utils.ReleaseVerifyHelper;
import com.pt.zyfooai.utils.RemoteConfigHelper;
import com.pt.zyfooai.utils.ThemeHelper;

import com.pt.zyfooai.respository.FrameRepository;
import com.pt.zyfooai.utils.FrameCatalogProvider;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.Util;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;

public class MyApplication extends Application {

    public static Context context;
    public AppOpenManager appOpenAdManager;
    public static MyApplication myApplication;
    private static MyApplication mInstance;
    PreferenceManager preferenceManager;
    NetworkConnectivity networkConnectivity;
    int notificationId = 1;

    public void showAppOpenAds() {
        try {
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {
                if (preferenceManager.getBoolean(Constant.OPEN_APP_AD_ENABLED) && preferenceManager.getBoolean(Constant.ADS_ENABLE)) {
                    appOpenAdManager = new AppOpenManager(myApplication, preferenceManager.getString(Constant.OPEN_AD_ID));
                }
            }
        } catch (Exception e) {
            Util.showErrorLog(e.getMessage(), e);
        }
    }

    public static synchronized MyApplication getInstance() {
        MyApplication myApplication;
        synchronized (MyApplication.class) {
            synchronized (MyApplication.class) {
                myApplication = mInstance;
            }
        }
        return myApplication;
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.wrap(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = myApplication = this;
        mInstance = MyApplication.this;
        MyApplication.context = getApplicationContext();

        networkConnectivity = new NetworkConnectivity(this);
        preferenceManager = new PreferenceManager(this);

        FirebaseApp.initializeApp(this);
        ThemeHelper.applySavedTheme(this);
        RemoteConfigHelper.fetchAndActivate(this, null);
        ReleaseVerifyHelper.logBuildInfo(this);

        context = this;
        if (networkConnectivity.isConnected()) {
            AppConfig.IS_CONNECTED = true;
            FrameRepository.syncInBackground(this, updated -> {
                if (updated) {
                    FrameCatalogProvider.invalidate();
                }
            });
        } else {
            AppConfig.IS_CONNECTED = false;
        }

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());


        OneSignal.initWithContext(this);
        OneSignal.setAppId("1ebefd5e-98fb-4c22-a3c3-f1e79ec6738f");

//        OneSignal.setNotificationWillShowInForegroundHandler(new OnesignalNotificationService());
//        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
//            Log.d("onMessageReceived__", "Notification received: " + notificationReceivedEvent.getNotification());
//            notificationReceivedEvent.complete(notificationReceivedEvent.getNotification());
//        });
//        OneSignal.promptForPushNotifications();

        showAppOpenAds();

        // Admob Ads Initialization
        MobileAds.initialize(this, initializationStatus -> {

        });

    }

}
