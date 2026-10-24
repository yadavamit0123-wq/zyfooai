package com.pt.zyfooai.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class ReferralMeResponse {

    @SerializedName("success")
    public Object successRaw;

    @SerializedName(value = "referral_code", alternate = {"code", "reffer_id"})
    public String referralCode;

    @SerializedName(value = "signup_bonus", alternate = {"signupBonus"})
    public String signupBonus;

    @SerializedName(value = "referrer_amount", alternate = {"referrerAmount"})
    public String referrerAmount;

    @SerializedName(value = "reffer_level_income", alternate = {"refer_level_income"})
    public JsonElement refferLevelIncome;

    @SerializedName(value = "invite_message_prefix", alternate = {"inviteMessagePrefix"})
    public String inviteMessagePrefix;

    @SerializedName(value = "invite_message_suffix", alternate = {"inviteMessageSuffix"})
    public String inviteMessageSuffix;

    @SerializedName(value = "invite_url", alternate = {"inviteUrl"})
    public String inviteUrl;

    @SerializedName(value = "share_text", alternate = {"shareText"})
    public String shareText;

    @SerializedName(value = "wallet_balance", alternate = {"walletBalance"})
    public String walletBalance;

    @SerializedName(value = "banner_type", alternate = {"bannerType"})
    public String bannerType;

    @SerializedName(value = "banner_url", alternate = {"bannerUrl"})
    public String bannerUrl;

    @SerializedName(value = "banner_poster_url", alternate = {"bannerPosterUrl", "banner_poster"})
    public String bannerPosterUrl;

    @SerializedName("message")
    public String message;

    /** Some APIs nest fields under data. */
    @SerializedName("data")
    public ReferralMeResponse data;

    public ReferralMeResponse resolved() {
        return data != null && (referralCode == null || referralCode.isEmpty()) ? data : this;
    }

    public String resolveReferrerAmount() {
        if (referrerAmount != null && !referrerAmount.isEmpty()) {
            return referrerAmount;
        }
        if (refferLevelIncome != null && refferLevelIncome.isJsonObject()) {
            com.google.gson.JsonObject obj = refferLevelIncome.getAsJsonObject();
            if (obj.has("1")) {
                return obj.get("1").getAsString();
            }
        }
        return "";
    }
}
