package com.pt.zyfooai.utils;

import android.content.Context;

import com.pt.zyfooai.model.FrameConfig;

import java.util.List;

/**
 * Local frame usage counters — can be synced to analytics backend later.
 */
public final class FrameAnalyticsHelper {

    private static final String KEY_PREFIX = "frame_use_";

    private FrameAnalyticsHelper() {
    }

    public static void trackSelection(Context context, FrameMediaType mediaType, int index) {
        if (context == null || mediaType == null || index < 0) {
            return;
        }
        String frameKey = resolveFrameKey(context, mediaType, index);
        if (frameKey.isEmpty()) {
            return;
        }
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String prefKey = KEY_PREFIX + mediaType.name().toLowerCase() + "_" + frameKey;
        preferenceManager.setInt(prefKey, preferenceManager.getInt(prefKey, 0) + 1);
        preferenceManager.setInt(KEY_PREFIX + "last_" + mediaType.name().toLowerCase(), index);
    }

    private static String resolveFrameKey(Context context, FrameMediaType mediaType, int index) {
        List<FrameEntry> entries = mediaType == FrameMediaType.REELS
                ? FrameCatalogProvider.videoFrameEntries(context)
                : FrameCatalogProvider.imageFrameEntries(context);
        if (index >= entries.size()) {
            return "";
        }
        FrameEntry entry = entries.get(index);
        if (entry.isDynamic()) {
            FrameConfig config = entry.getDynamicConfig();
            if (config != null && config.id != null && !config.id.isEmpty()) {
                return config.id;
            }
            return "dynamic_" + index;
        }
        return "xml_" + entry.getLayoutResId();
    }
}
