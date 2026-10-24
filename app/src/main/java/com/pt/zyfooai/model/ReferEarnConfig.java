package com.pt.zyfooai.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Soft-extend from getAppInfo → {@code ReferEarn} (and optional flat keys on AppInfo).
 */
public class ReferEarnConfig {

    @SerializedName(value = "refer_earn_enabled", alternate = {"enabled", "referEarnEnabled"})
    public Object referEarnEnabled;

    @SerializedName(value = "signup_bonus", alternate = {"signupBonus"})
    public String signupBonus;

    @SerializedName(value = "reffer_level_income", alternate = {"refer_level_income", "refferLevelIncome"})
    public JsonElement refferLevelIncome;

    @SerializedName(value = "referrer_amount", alternate = {"referrerAmount", "level_1", "level1"})
    public String referrerAmount;

    @SerializedName(value = "invite_message_prefix", alternate = {"inviteMessagePrefix"})
    public String inviteMessagePrefix;

    @SerializedName(value = "invite_message_suffix", alternate = {"inviteMessageSuffix"})
    public String inviteMessageSuffix;

    @SerializedName(value = "invite_url", alternate = {"inviteUrl", "share_url"})
    public String inviteUrl;

    @SerializedName(value = "share_text", alternate = {"shareText"})
    public String shareText;

    @SerializedName(value = "banner_type", alternate = {"bannerType"})
    public String bannerType;

    @SerializedName(value = "banner_url", alternate = {"bannerUrl"})
    public String bannerUrl;

    @SerializedName(value = "banner_poster_url", alternate = {"bannerPosterUrl", "banner_poster"})
    public String bannerPosterUrl;

    public boolean isEnabled() {
        if (referEarnEnabled == null) {
            return false;
        }
        if (referEarnEnabled instanceof Boolean) {
            return (Boolean) referEarnEnabled;
        }
        if (referEarnEnabled instanceof Number) {
            return ((Number) referEarnEnabled).intValue() == 1;
        }
        String s = String.valueOf(referEarnEnabled).trim();
        return "1".equals(s) || "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
    }

    /** Level-1 referrer reward from JSON map or flat field. */
    public String resolveReferrerAmount() {
        if (referrerAmount != null && !referrerAmount.isEmpty()) {
            return referrerAmount;
        }
        if (refferLevelIncome != null && refferLevelIncome.isJsonObject()) {
            com.google.gson.JsonObject obj = refferLevelIncome.getAsJsonObject();
            if (obj.has("1")) {
                return obj.get("1").getAsString();
            }
            if (obj.has("level_1")) {
                return obj.get("level_1").getAsString();
            }
            if (obj.has("level1")) {
                return obj.get("level1").getAsString();
            }
        }
        return "";
    }
}
