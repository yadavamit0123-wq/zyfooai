package com.pt.zyfooai.utils;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pt.zyfooai.BuildConfig;

public final class RemoteConfigHelper {

    public static final String KEY_API = "apiKey";
    public static final String KEY_FAST2SMS = "fast2sms_key";
    public static final String KEY_HOME_LAYOUT = "home_layout_variant";
    public static final String KEY_NATIVE_AD_INTERVAL = "native_ad_interval";
    public static final String KEY_FORCE_UPDATE = "force_update_version_code";
    public static final String KEY_UPDATE_MESSAGE = "update_message";

    private RemoteConfigHelper() {
    }

    public static void fetchAndActivate(Context context, Runnable onComplete) {
        FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        config.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : 3600)
                .build());
        java.util.Map<String, Object> defaults = new java.util.HashMap<>();
        defaults.put(KEY_HOME_LAYOUT, "A");
        defaults.put(KEY_NATIVE_AD_INTERVAL, 6L);
        defaults.put(KEY_FORCE_UPDATE, 0L);
        defaults.put(KEY_UPDATE_MESSAGE, "A new version is available. Please update.");
        config.setDefaultsAsync(defaults);
        config.fetchAndActivate().addOnCompleteListener(task -> {
            PreferenceManager prefs = new PreferenceManager(context);
            String apiKey = config.getString(KEY_API);
            if (apiKey != null && !apiKey.isEmpty()) {
                prefs.setString(Constant.API_KEY, apiKey);
            }
            String smsKey = config.getString(KEY_FAST2SMS);
            if (smsKey != null && !smsKey.isEmpty()) {
                prefs.setString(Constant.FAST2SMS_KEY, smsKey);
            }
            prefs.setString(Constant.HOME_LAYOUT_VARIANT, config.getString(KEY_HOME_LAYOUT));
            prefs.setInt(Constant.NATIVE_AD_INTERVAL, (int) config.getLong(KEY_NATIVE_AD_INTERVAL));
            prefs.setInt(Constant.FORCE_UPDATE_VERSION, (int) config.getLong(KEY_FORCE_UPDATE));
            prefs.setString(Constant.UPDATE_MESSAGE, config.getString(KEY_UPDATE_MESSAGE));
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public static boolean isLayoutVariantB(Context context) {
        return "B".equalsIgnoreCase(new PreferenceManager(context).getString(Constant.HOME_LAYOUT_VARIANT));
    }

    public static int getNativeAdInterval(Context context) {
        int interval = new PreferenceManager(context).getInt(Constant.NATIVE_AD_INTERVAL);
        return interval > 0 ? interval : 6;
    }

    public static String getFast2SmsKey(Context context) {
        PreferenceManager prefs = new PreferenceManager(context);
        String remoteKey = prefs.getString(Constant.FAST2SMS_KEY);
        if (remoteKey != null && !remoteKey.isEmpty()) {
            return remoteKey;
        }
        return BuildConfig.FAST2SMS_KEY;
    }
}
