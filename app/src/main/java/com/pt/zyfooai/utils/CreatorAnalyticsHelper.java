package com.pt.zyfooai.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.pt.zyfooai.data.AppDatabase;
import com.pt.zyfooai.data.entity.PostAnalyticsEntity;

public final class CreatorAnalyticsHelper {

    private CreatorAnalyticsHelper() {
    }

    public static void trackDownload(Context context, String postId, String categoryId) {
        if (postId == null || postId.isEmpty()) {
            return;
        }
        AnalyticsHelper.logPostEvent(context, "post_download", postId, categoryId);
        persistAsync(context, postId, categoryId, true);
    }

    public static void trackShare(Context context, String postId, String categoryId) {
        if (postId == null || postId.isEmpty()) {
            return;
        }
        AnalyticsHelper.logPostEvent(context, "post_share", postId, categoryId);
        persistAsync(context, postId, categoryId, false);
    }

    public static void trackCategoryView(Context context, String categoryId, String categoryName) {
        AnalyticsHelper.logEvent(context, "category_view", "category_id", categoryId != null ? categoryId : "");
        if (categoryName != null && !categoryName.isEmpty()) {
            AnalyticsHelper.logEvent(context, "category_view", "category_name", categoryName);
        }
    }

    private static void persistAsync(Context context, String postId, String categoryId, boolean download) {
        AsyncTask.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            PostAnalyticsEntity existing = db.postAnalyticsDao().getByPostId(postId);
            if (existing == null) {
                existing = new PostAnalyticsEntity(postId, categoryId);
            }
            if (download) {
                existing.downloadCount++;
            } else {
                existing.shareCount++;
            }
            existing.lastUpdated = System.currentTimeMillis();
            db.postAnalyticsDao().upsert(existing);
        });
    }
}
