package com.pt.zyfooai.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemeHelper {

    public static final String DARK_MODE_ENABLED = "dark_mode_enabled";

    private ThemeHelper() {
    }

    public static void applySavedTheme(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        if (preferenceManager.getBoolean(DARK_MODE_ENABLED)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void setDarkMode(Context context, boolean enabled) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setBoolean(DARK_MODE_ENABLED, enabled);
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static boolean isDarkModeEnabled(Context context) {
        return new PreferenceManager(context).getBoolean(DARK_MODE_ENABLED);
    }
}
