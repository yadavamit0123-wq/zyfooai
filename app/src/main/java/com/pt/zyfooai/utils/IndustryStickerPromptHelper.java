package com.pt.zyfooai.utils;

import android.content.Context;

import com.pt.zyfooai.R;

import java.util.Locale;

/**
 * P1: category-aware "Add yours" sticker prompts for industry feeds.
 */
public final class IndustryStickerPromptHelper {

    private IndustryStickerPromptHelper() {
    }

    public static String promptText(Context context, PreferenceManager preferenceManager) {
        if (context == null || preferenceManager == null) {
            return "";
        }
        String seasonalPrompt = SeasonalStickerHelper.getActivePrompt(context, preferenceManager);
        if (seasonalPrompt != null && !seasonalPrompt.isEmpty()) {
            return seasonalPrompt;
        }
        String categoryId = preferenceManager.getString(Constant.SELECTED_CATEGORY_ID);
        String categoryName = preferenceManager.getString(Constant.SELECTED_CATEGORY_NAME);
        boolean isBusiness = !"Personal".equals(preferenceManager.getString(Constant.DEFAULT_TYPE));
        int promptRes = promptResForCategory(categoryId, categoryName, isBusiness);
        return context.getString(promptRes);
    }

    public static int promptResForCategory(String categoryId, String categoryName, boolean isBusiness) {
        if ("-4".equals(categoryId)) {
            return R.string.sticker_prompt_political;
        }
        if ("-3".equals(categoryId)) {
            return R.string.sticker_prompt_shop;
        }

        if (categoryName != null && !categoryName.trim().isEmpty()) {
            String lower = categoryName.toLowerCase(Locale.getDefault());
            if (containsAny(lower,
                    "restaurant", "food", "cafe", "café", "hotel", "dhaba", "kitchen", "catering", "pizza", "biryani")) {
                return R.string.sticker_prompt_restaurant;
            }
            if (containsAny(lower,
                    "salon", "beauty", "parlour", "parlor", "spa", "barber", "hair", "makeup", "nail")) {
                return R.string.sticker_prompt_salon;
            }
            if (containsAny(lower,
                    "real estate", "realestate", "property", "realtor", "builder", "construction", "flat", "plot", "home")) {
                return R.string.sticker_prompt_real_estate;
            }
            if (containsAny(lower,
                    "political", "politics", "election", "party", "leader", "vote", "bjp", "congress")) {
                return R.string.sticker_prompt_political;
            }
            if (containsAny(lower,
                    "shop", "store", "mart", "retail", "market", "boutique", "deal", "offer", "sale", "business")) {
                return R.string.sticker_prompt_shop;
            }
        }

        return isBusiness ? R.string.sticker_prompt_business : R.string.sticker_prompt_personal;
    }

    private static boolean containsAny(String haystack, String... needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
