package com.pt.zyfooai.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.pt.zyfooai.model.AiConfig;
import com.pt.zyfooai.model.AiQuota;
import com.pt.zyfooai.model.AppInfos;

import java.util.Collections;
import java.util.List;

/**
 * Cached AI config from getAppInfo / GET /api/ai/config.
 */
public final class AiPrefs {

    private static final String KEY_JSON = "ai_config_json";
    private static final String KEY_ENABLED = "ai_enabled";
    private static final String KEY_DEFAULT_PROVIDER = "ai_default_provider";
    private static final Gson GSON = new Gson();

    private AiPrefs() {
    }

    public static void saveFromAppInfo(Context context, AppInfos appInfos) {
        if (context == null || appInfos == null || appInfos.ai == null) {
            return;
        }
        save(context, appInfos.ai);
    }

    public static void save(Context context, AiConfig config) {
        if (context == null || config == null) {
            return;
        }
        PreferenceManager prefs = new PreferenceManager(context);
        prefs.setString(KEY_JSON, GSON.toJson(config));
        prefs.setBoolean(KEY_ENABLED, config.isEnabled());
        String def = config.resolveDefaultProvider();
        if (!TextUtils.isEmpty(def)) {
            prefs.setString(KEY_DEFAULT_PROVIDER, def);
        }
        if (config.quota != null) {
            applyQuota(prefs, config.quota);
        }
    }

    public static void applyQuota(Context context, AiQuota quota) {
        if (context == null || quota == null) {
            return;
        }
        applyQuota(new PreferenceManager(context), quota);
    }

    private static void applyQuota(PreferenceManager prefs, AiQuota quota) {
        if (quota.remainingChat != null) {
            prefs.setInt("ai_remaining_chat", quota.remainingChat);
        }
        if (quota.remainingImage != null) {
            prefs.setInt("ai_remaining_image", quota.remainingImage);
        }
        if (quota.remainingProcess != null) {
            prefs.setInt("ai_remaining_process", quota.remainingProcess);
        }
    }

    public static AiConfig get(Context context) {
        if (context == null) {
            return null;
        }
        String json = new PreferenceManager(context).getString(KEY_JSON);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, AiConfig.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isEnabled(Context context) {
        if (context == null) {
            return false;
        }
        PreferenceManager prefs = new PreferenceManager(context);
        if (prefs.getBoolean(KEY_ENABLED)) {
            return true;
        }
        AiConfig cfg = get(context);
        return cfg != null && cfg.isEnabled();
    }

    public static List<String> providers(Context context) {
        AiConfig cfg = get(context);
        if (cfg == null) {
            return Collections.emptyList();
        }
        return cfg.resolvedProviders();
    }

    public static String defaultProvider(Context context) {
        AiConfig cfg = get(context);
        if (cfg != null) {
            String d = cfg.resolveDefaultProvider();
            if (!TextUtils.isEmpty(d)) {
                return d;
            }
        }
        return new PreferenceManager(context).getString(KEY_DEFAULT_PROVIDER);
    }

    public static boolean hasTool(Context context, String tool) {
        AiConfig cfg = get(context);
        return cfg != null && cfg.hasTool(tool);
    }
}
