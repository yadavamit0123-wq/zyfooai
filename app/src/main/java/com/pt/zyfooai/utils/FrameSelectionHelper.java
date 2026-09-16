package com.pt.zyfooai.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Random;

/**
 * Default frame position logic for sticker-first frame list (P0/P1).
 * Image and reels selections are stored independently.
 */
public final class FrameSelectionHelper {

    /** @deprecated Legacy single index — migrated to {@link #USER_SELECTED_IMAGE_FRAME_INDEX}. */
    public static final String USER_SELECTED_FRAME_INDEX = "user_selected_frame_index";
    public static final String USER_SELECTED_IMAGE_FRAME_INDEX = "user_selected_image_frame_index";
    public static final String USER_SELECTED_REELS_FRAME_INDEX = "user_selected_reels_frame_index";
    public static final String FRAME_SELECTION_SPLIT_MIGRATED = "frame_selection_split_migrated";

    public static final int NO_USER_SELECTION = -1;

    /** Personal default: glass chip frame. */
    public static final int DEFAULT_PERSONAL_STICKER_INDEX = ModernFrameCatalog.INDEX_STICKER_GLASS_CHIP;

    /** Business / seasonal default: primary sticker prompt frame. */
    public static final int DEFAULT_BUSINESS_STICKER_INDEX = ModernFrameCatalog.INDEX_STICKER_PROMPT;

    private FrameSelectionHelper() {
    }

    public static boolean prefersDarkGoldFrame(PreferenceManager preferenceManager) {
        if (preferenceManager == null) {
            return false;
        }
        if (!"Personal".equals(preferenceManager.getString(Constant.DEFAULT_TYPE))) {
            return true;
        }
        return isPoliticalCategory(preferenceManager);
    }

    public static boolean isPoliticalCategory(PreferenceManager preferenceManager) {
        if ("-4".equals(preferenceManager.getString(Constant.SELECTED_CATEGORY_ID))) {
            return true;
        }
        String categoryName = preferenceManager.getString(Constant.SELECTED_CATEGORY_NAME);
        return categoryName != null
                && categoryName.toLowerCase(Locale.getDefault()).contains("political");
    }

    public static int defaultFramePosition(PreferenceManager preferenceManager, FrameMediaType mediaType, Random random) {
        if (SeasonalStickerHelper.prefersSeasonalFrame(preferenceManager)) {
            return DEFAULT_BUSINESS_STICKER_INDEX;
        }
        if (prefersDarkGoldFrame(preferenceManager)) {
            return DEFAULT_BUSINESS_STICKER_INDEX;
        }
        if (random != null) {
            return random.nextBoolean()
                    ? ModernFrameCatalog.INDEX_STICKER_GLASS_CHIP
                    : ModernFrameCatalog.INDEX_STICKER_CORNER;
        }
        return DEFAULT_PERSONAL_STICKER_INDEX;
    }

    public static int getActiveFramePosition(PreferenceManager preferenceManager, FrameMediaType mediaType) {
        return getActiveFramePosition(preferenceManager, mediaType, new Random());
    }

    public static int getActiveFramePosition(
            PreferenceManager preferenceManager,
            FrameMediaType mediaType,
            Random random
    ) {
        if (preferenceManager == null || mediaType == null) {
            return DEFAULT_PERSONAL_STICKER_INDEX;
        }
        migrateLegacySelectionIfNeeded(preferenceManager);

        int saved = preferenceManager.getInt(preferenceKeyFor(mediaType), NO_USER_SELECTION);
        if (saved >= 0) {
            int normalized = ModernFrameCatalog.normalizeSavedIndex(preferenceManager, saved, mediaType);
            if (normalized >= 0 && normalized < ModernFrameCatalog.frameCount(mediaType)) {
                return normalized;
            }
        }
        return defaultFramePosition(preferenceManager, mediaType, random);
    }

    /** @deprecated Use {@link #getActiveFramePosition(PreferenceManager, FrameMediaType)}. */
    @Deprecated
    public static int getActiveFeedFramePosition(PreferenceManager preferenceManager) {
        return getActiveFramePosition(preferenceManager, FrameMediaType.IMAGE, new Random());
    }

    /** @deprecated Use {@link #getActiveFramePosition(PreferenceManager, FrameMediaType, Random)}. */
    @Deprecated
    public static int getActiveFeedFramePosition(PreferenceManager preferenceManager, Random random) {
        return getActiveFramePosition(preferenceManager, FrameMediaType.IMAGE, random);
    }

    public static int defaultSaveFramePosition(PreferenceManager preferenceManager) {
        return getActiveFramePosition(preferenceManager, FrameMediaType.IMAGE, null);
    }

    public static void saveUserFrameSelection(
            PreferenceManager preferenceManager,
            int index,
            FrameMediaType mediaType
    ) {
        if (preferenceManager == null || mediaType == null || index < 0) {
            return;
        }
        if (index >= ModernFrameCatalog.frameCount(mediaType)) {
            return;
        }
        preferenceManager.setInt(preferenceKeyFor(mediaType), index);
        preferenceManager.setInt(ModernFrameCatalog.FRAME_CATALOG_VERSION_KEY, ModernFrameCatalog.FRAME_CATALOG_VERSION);
    }

    /** @deprecated Use {@link #saveUserFrameSelection(PreferenceManager, int, FrameMediaType)}. */
    @Deprecated
    public static void saveUserFrameSelection(PreferenceManager preferenceManager, int index) {
        saveUserFrameSelection(preferenceManager, index, FrameMediaType.IMAGE);
    }

    public static String preferenceKeyFor(FrameMediaType mediaType) {
        if (mediaType == FrameMediaType.REELS) {
            return USER_SELECTED_REELS_FRAME_INDEX;
        }
        return USER_SELECTED_IMAGE_FRAME_INDEX;
    }

    public static int visibleFrameIndex(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return NO_USER_SELECTION;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return NO_USER_SELECTION;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) {
            position = linearLayoutManager.findFirstVisibleItemPosition();
        }
        return position;
    }

    private static void migrateLegacySelectionIfNeeded(PreferenceManager preferenceManager) {
        if (preferenceManager.getBoolean(FRAME_SELECTION_SPLIT_MIGRATED, false)) {
            return;
        }

        int legacy = preferenceManager.getInt(USER_SELECTED_FRAME_INDEX, NO_USER_SELECTION);
        if (legacy >= 0) {
            int normalized = ModernFrameCatalog.normalizeSavedIndex(
                    preferenceManager,
                    legacy,
                    FrameMediaType.IMAGE
            );
            preferenceManager.setInt(USER_SELECTED_IMAGE_FRAME_INDEX, normalized);
            preferenceManager.setInt(USER_SELECTED_REELS_FRAME_INDEX, normalized);
        }

        preferenceManager.setBoolean(FRAME_SELECTION_SPLIT_MIGRATED, true);
    }

}
