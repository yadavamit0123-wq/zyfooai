package com.pt.zyfooai.utils;

import java.util.Locale;
import java.util.Random;

/**
 * Default frame position logic for sticker-first frame list (P0/P1).
 */
public final class FrameSelectionHelper {

    public static final int STICKER_FRAME_COUNT = 8;

    /** Personal default: minimal glass chip (sticker 3). */
    public static final int DEFAULT_PERSONAL_STICKER_INDEX = 2;

    /** Dark gold premium sticker (sticker 5). */
    public static final int DEFAULT_DARK_GOLD_STICKER_INDEX = 4;

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
            return DEFAULT_DARK_GOLD_STICKER_INDEX;
        }
        if (prefersDarkGoldFrame(preferenceManager)) {
            return DEFAULT_DARK_GOLD_STICKER_INDEX;
        }
        if (random != null) {
            return DEFAULT_PERSONAL_STICKER_INDEX + random.nextInt(2);
        }
        return DEFAULT_PERSONAL_STICKER_INDEX;
    }

    public static int defaultSaveFramePosition(PreferenceManager preferenceManager) {
        if (SeasonalStickerHelper.prefersSeasonalFrame(preferenceManager)
                || prefersDarkGoldFrame(preferenceManager)) {
            return DEFAULT_DARK_GOLD_STICKER_INDEX;
        }
        return DEFAULT_PERSONAL_STICKER_INDEX;
    }

    /** Business users: QR card frame index in {@link ModernFrameCatalog}. */
    public static int qrFrameIndex() {
        return ModernFrameCatalog.INDEX_STICKER_QR;
    }
}
