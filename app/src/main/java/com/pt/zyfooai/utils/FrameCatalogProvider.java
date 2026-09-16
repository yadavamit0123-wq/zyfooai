package com.pt.zyfooai.utils;

import android.content.Context;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.FrameConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the active frame carousel from bundled XML layouts plus cached/server PNG frames.
 */
public final class FrameCatalogProvider {

    private static volatile List<FrameEntry> cachedImageEntries;
    private static volatile List<FrameEntry> cachedVideoEntries;

    private FrameCatalogProvider() {
    }

    public static void invalidate() {
        cachedImageEntries = null;
        cachedVideoEntries = null;
    }

    public static List<FrameEntry> imageFrameEntries(Context context) {
        if (cachedImageEntries == null) {
            cachedImageEntries = buildEntries(context, FrameMediaType.IMAGE);
        }
        return cachedImageEntries;
    }

    public static List<FrameEntry> videoFrameEntries(Context context) {
        if (cachedVideoEntries == null) {
            cachedVideoEntries = buildEntries(context, FrameMediaType.REELS);
        }
        return cachedVideoEntries;
    }

    public static int frameCount(Context context, FrameMediaType mediaType) {
        return mediaType == FrameMediaType.REELS
                ? videoFrameEntries(context).size()
                : imageFrameEntries(context).size();
    }

    private static List<FrameEntry> buildEntries(Context context, FrameMediaType mediaType) {
        List<FrameEntry> entries = new ArrayList<>();
        List<Integer> xmlLayouts = mediaType == FrameMediaType.REELS
                ? ModernFrameCatalog.videoFrameLayouts()
                : ModernFrameCatalog.imageFrameLayouts();
        for (int layoutResId : xmlLayouts) {
            entries.add(FrameEntry.xml(layoutResId));
        }

        List<FrameConfig> remoteFrames = FrameCache.publishedFrames(FrameCache.load(context, mediaType));
        if (!remoteFrames.isEmpty()) {
            int dynamicLayoutResId = mediaType == FrameMediaType.REELS
                    ? R.layout.layout_video_frame_dynamic
                    : R.layout.layout_frame_dynamic;
            for (FrameConfig config : remoteFrames) {
                if (config.resolvedMediaType() == mediaType) {
                    entries.add(FrameEntry.dynamic(dynamicLayoutResId, config));
                }
            }
        } else if (DynamicFrameCatalog.INCLUDE_TEST_FRAMES) {
            DynamicFrameCatalog.appendTestFrames(context, entries, mediaType);
        }

        return entries;
    }
}
