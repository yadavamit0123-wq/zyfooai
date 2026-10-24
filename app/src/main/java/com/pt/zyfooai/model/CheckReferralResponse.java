package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class CheckReferralResponse {

    @SerializedName("valid")
    public Object validRaw;

    @SerializedName("message")
    public String message;

    @SerializedName(value = "referrer_name", alternate = {"referrerName"})
    public String referrerName;

    public boolean isValid() {
        if (validRaw == null) {
            return false;
        }
        if (validRaw instanceof Boolean) {
            return (Boolean) validRaw;
        }
        if (validRaw instanceof Number) {
            return ((Number) validRaw).intValue() == 1;
        }
        String s = String.valueOf(validRaw);
        return "1".equals(s) || "true".equalsIgnoreCase(s);
    }
}
