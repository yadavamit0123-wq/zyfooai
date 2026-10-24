package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class ContentReportResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName("message")
    public String message;

    /** HTTP status when call failed (set by repository). */
    public transient int httpCode;

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
