package com.pt.zyfooai.utils;

import android.content.Context;

import com.pt.zyfooai.MyApplication;
import com.pt.zyfooai.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Curated modern frame pack — low-quality / legacy layouts removed in Phase 5.
 * Image and reels catalogs are parallel but selected independently in the app.
 * Admin-published PNG overlays append via {@link FrameCatalogProvider}.
 */
public final class ModernFrameCatalog {

    public static final int INDEX_STICKER_PROMPT = 0;
    public static final int INDEX_STICKER_GLASS_CHIP = 1;
    public static final int INDEX_STICKER_CORNER = 2;
    public static final int INDEX_GLASS_DARK = 3;
    public static final int INDEX_GRADIENT_PURPLE = 4;
    public static final int INDEX_GRADIENT_GOLD = 5;

    public static final String FRAME_CATALOG_VERSION_KEY = "frame_catalog_version";
    public static final int FRAME_CATALOG_VERSION = 3;

    /** Maps legacy 12-frame carousel indices to the current catalog. */
    private static final int[] LEGACY_INDEX_MAP = {
            0, // 0 sticker prompt
            0, // 1 nametag (removed)
            1, // 2 glass chip
            2, // 3 corner
            0, // 4 dark gold (removed)
            0, // 5 quote (removed)
            0, // 6 reels (removed)
            0, // 7 qr (removed)
            3, // 8 glass light (removed) → dark glass
            3, // 9 glass dark
            4, // 10 gradient purple
            5, // 11 gradient gold
    };

    private ModernFrameCatalog() {
    }

    public static List<Integer> frameLayouts(FrameMediaType mediaType) {
        if (mediaType == FrameMediaType.REELS) {
            return videoFrameLayouts();
        }
        return imageFrameLayouts();
    }

    public static List<Integer> imageFrameLayouts() {
        List<Integer> layouts = new ArrayList<>();
        layouts.add(R.layout.layout_frame_sticker_1);
        layouts.add(R.layout.layout_frame_sticker_3);
        layouts.add(R.layout.layout_frame_sticker_4);
        layouts.add(R.layout.layout_frame_glass_2);
        layouts.add(R.layout.layout_frame_gradient_1);
        layouts.add(R.layout.layout_frame_gradient_2);
        return Collections.unmodifiableList(layouts);
    }

    public static List<Integer> videoFrameLayouts() {
        List<Integer> layouts = new ArrayList<>();
        layouts.add(R.layout.layout_video_frame_sticker_1);
        layouts.add(R.layout.layout_video_frame_sticker_3);
        layouts.add(R.layout.layout_video_frame_sticker_4);
        layouts.add(R.layout.layout_video_frame_glass_2);
        layouts.add(R.layout.layout_video_frame_gradient_1);
        layouts.add(R.layout.layout_video_frame_gradient_2);
        return Collections.unmodifiableList(layouts);
    }

    public static List<FrameEntry> imageFrameEntries(Context context) {
        if (context == null) {
            context = MyApplication.getAppContext();
        }
        return context != null
                ? FrameCatalogProvider.imageFrameEntries(context)
                : Collections.emptyList();
    }

    public static List<FrameEntry> videoFrameEntries(Context context) {
        if (context == null) {
            context = MyApplication.getAppContext();
        }
        return context != null
                ? FrameCatalogProvider.videoFrameEntries(context)
                : Collections.emptyList();
    }

    public static int frameCount() {
        return frameCount(FrameMediaType.IMAGE);
    }

    public static int frameCount(FrameMediaType mediaType) {
        Context context = MyApplication.getAppContext();
        if (context != null) {
            return FrameCatalogProvider.frameCount(context, mediaType);
        }
        return frameLayouts(mediaType).size() + DynamicFrameCatalog.testFrameCount();
    }

    public static int normalizeSavedIndex(PreferenceManager preferenceManager, int savedIndex) {
        return normalizeSavedIndex(preferenceManager, savedIndex, FrameMediaType.IMAGE);
    }

    public static int normalizeSavedIndex(
            PreferenceManager preferenceManager,
            int savedIndex,
            FrameMediaType mediaType
    ) {
        if (savedIndex < 0) {
            return savedIndex;
        }
        int count = frameCount(mediaType);
        if (preferenceManager != null
                && preferenceManager.getInt(FRAME_CATALOG_VERSION_KEY, 1) >= FRAME_CATALOG_VERSION) {
            return savedIndex >= count ? 0 : savedIndex;
        }
        int migrated = savedIndex < LEGACY_INDEX_MAP.length ? LEGACY_INDEX_MAP[savedIndex] : 0;
        if (preferenceManager != null) {
            preferenceManager.setInt(FrameSelectionHelper.preferenceKeyFor(mediaType), migrated);
            preferenceManager.setInt(FRAME_CATALOG_VERSION_KEY, FRAME_CATALOG_VERSION);
        }
        return migrated >= count ? 0 : migrated;
    }
}
