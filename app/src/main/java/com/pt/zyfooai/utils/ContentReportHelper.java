package com.pt.zyfooai.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.ui.activities.ReportContentActivity;

/**
 * Opens Report Content screen. Does not touch feed download/footer logic.
 */
public final class ContentReportHelper {

    public static final String EXTRA_POST_ID = "post_id";
    public static final String EXTRA_POST_TYPE = "post_type";
    public static final String EXTRA_THUMBNAIL = "post_thumbnail_url";
    public static final String EXTRA_FRAME_ID = "frame_id";

    private ContentReportHelper() {
    }

    public static void open(@NonNull Context context, @Nullable PostItem post) {
        if (post == null || TextUtils.isEmpty(post.postId)) {
            return;
        }
        Intent intent = new Intent(context, ReportContentActivity.class);
        intent.putExtra(EXTRA_POST_ID, post.postId);
        intent.putExtra(EXTRA_POST_TYPE, resolvePostType(post));
        String thumb = !TextUtils.isEmpty(post.thumbnail) ? post.thumbnail : post.image_url;
        if (!TextUtils.isEmpty(thumb)) {
            intent.putExtra(EXTRA_THUMBNAIL, thumb);
        }
        context.startActivity(intent);
    }

    @NonNull
    public static String resolvePostType(@Nullable PostItem post) {
        if (post == null) {
            return "image";
        }
        if (!TextUtils.isEmpty(post.type)) {
            String t = post.type.trim().toLowerCase();
            if ("reels".equals(t)) {
                return "reel";
            }
            if ("image".equals(t) || "video".equals(t) || "reel".equals(t)) {
                return t;
            }
        }
        return post.is_video ? "reel" : "image";
    }
}
