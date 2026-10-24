package com.pt.zyfooai.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Client-side 24h guard for same user + post (API also returns 429).
 */
public final class ContentReportPrefs {

    private static final String PREF = "content_report_prefs";
    private static final String KEY_PREFIX = "reported_at_";
    private static final long WINDOW_MS = 24L * 60L * 60L * 1000L;

    private ContentReportPrefs() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    private static String key(String userId, String postId) {
        return KEY_PREFIX + userId + "_" + postId;
    }

    public static boolean wasReportedRecently(Context context, String userId, String postId) {
        if (context == null || TextUtils.isEmpty(userId) || TextUtils.isEmpty(postId)) {
            return false;
        }
        long at = prefs(context).getLong(key(userId, postId), 0L);
        return at > 0 && (System.currentTimeMillis() - at) < WINDOW_MS;
    }

    public static void markReported(Context context, String userId, String postId) {
        if (context == null || TextUtils.isEmpty(userId) || TextUtils.isEmpty(postId)) {
            return;
        }
        prefs(context).edit().putLong(key(userId, postId), System.currentTimeMillis()).apply();
    }
}
