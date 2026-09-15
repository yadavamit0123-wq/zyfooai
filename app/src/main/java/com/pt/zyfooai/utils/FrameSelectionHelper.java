package com.pt.zyfooai.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Random;

/**
 * Default frame position logic for sticker-first frame list (P0/P1).
 */
public final class FrameSelectionHelper {

    public static final String USER_SELECTED_FRAME_INDEX = "user_selected_frame_index";
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

    public static int defaultFeedFramePosition(PreferenceManager preferenceManager, Random random) {
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

    /** Saved user frame, or smart default on first use. */
    public static int getActiveFeedFramePosition(PreferenceManager preferenceManager) {
        return getActiveFeedFramePosition(preferenceManager, new Random());
    }

    public static int getActiveFeedFramePosition(PreferenceManager preferenceManager, Random random) {
        if (preferenceManager == null) {
            return DEFAULT_PERSONAL_STICKER_INDEX;
        }
        int saved = preferenceManager.getInt(USER_SELECTED_FRAME_INDEX, NO_USER_SELECTION);
        if (saved >= 0) {
            int normalized = ModernFrameCatalog.normalizeSavedIndex(preferenceManager, saved);
            if (normalized >= 0 && normalized < ModernFrameCatalog.frameCount()) {
                return normalized;
            }
        }
        return defaultFeedFramePosition(preferenceManager, random);
    }

    public static int defaultSaveFramePosition(PreferenceManager preferenceManager) {
        return getActiveFeedFramePosition(preferenceManager, null);
    }

    public static void saveUserFrameSelection(PreferenceManager preferenceManager, int index) {
        if (preferenceManager == null || index < 0) {
            return;
        }
        if (index >= ModernFrameCatalog.frameCount()) {
            return;
        }
        preferenceManager.setInt(USER_SELECTED_FRAME_INDEX, index);
        preferenceManager.setInt(ModernFrameCatalog.FRAME_CATALOG_VERSION_KEY, ModernFrameCatalog.FRAME_CATALOG_VERSION);
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

}
