package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class ApplyReferralResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName("wallet_balance")
    public String walletBalance;

    @SerializedName("signup_bonus_credited")
    public Object signupBonusCredited;

    @SerializedName("referrer_credited")
    public Object referrerCredited;

    @SerializedName("message")
    public String message;

    public boolean isSuccess() {
        if (successRaw == null) {
            return false;
        }
        if (successRaw instanceof Boolean) {
            return (Boolean) successRaw;
        }
        if (successRaw instanceof Number) {
            int n = ((Number) successRaw).intValue();
            return n == 1 || n == 200;
        }
        String s = String.valueOf(successRaw);
        return "1".equals(s) || "true".equalsIgnoreCase(s) || "200".equals(s);
    }
}
