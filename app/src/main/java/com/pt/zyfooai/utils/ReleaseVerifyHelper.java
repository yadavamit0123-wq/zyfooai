package com.pt.zyfooai.utils;

import android.content.Context;
import android.util.Log;

import com.pt.zyfooai.BuildConfig;

/**
 * Runtime sanity checks useful after ProGuard-enabled release builds.
 */
public final class ReleaseVerifyHelper {

    private static final String TAG = "ReleaseVerifyHelper";

    private ReleaseVerifyHelper() {
    }

    public static void logBuildInfo(Context context) {
        Log.i(TAG, "version=" + BuildConfig.VERSION_NAME
                + " code=" + BuildConfig.VERSION_CODE
                + " debug=" + BuildConfig.DEBUG);
        Log.i(TAG, "locale=" + LocaleHelper.getLocale(context)
                + " darkMode=" + ThemeHelper.isDarkModeEnabled(context));
    }

    public static boolean isCriticalConfigPresent(Context context) {
        PreferenceManager prefs = new PreferenceManager(context);
        boolean hasApiKey = prefs.getString(Constant.API_KEY) != null
                && !prefs.getString(Constant.API_KEY).isEmpty();
        if (!hasApiKey) {
            Log.w(TAG, "API key missing in preferences");
        }
        return hasApiKey;
    }
}
