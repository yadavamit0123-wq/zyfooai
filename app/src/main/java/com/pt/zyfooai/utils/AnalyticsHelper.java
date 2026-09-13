package com.pt.zyfooai.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public final class AnalyticsHelper {

    private AnalyticsHelper() {
    }

    public static void logEvent(Context context, String event, String key, String value) {
        if (context == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
    }

    public static void logScreen(Context context, String screenName) {
        logEvent(context, FirebaseAnalytics.Event.SCREEN_VIEW,
                FirebaseAnalytics.Param.SCREEN_NAME, screenName);
    }

    public static void logDownload(Context context, String mediaType) {
        logEvent(context, "content_download", "media_type", mediaType);
    }

    public static void logLogin(Context context, String method) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public static void logPostEvent(Context context, String event, String postId, String categoryId) {
        Bundle bundle = new Bundle();
        bundle.putString("post_id", postId != null ? postId : "");
        bundle.putString("category_id", categoryId != null ? categoryId : "");
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
    }

    public static void logCategorySelect(Context context, String categoryId, String categoryName) {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", categoryId != null ? categoryId : "");
        bundle.putString("category_name", categoryName != null ? categoryName : "");
        FirebaseAnalytics.getInstance(context).logEvent("select_category", bundle);
    }
}
