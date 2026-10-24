package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AiProcessResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName("message")
    public String message;

    @SerializedName(value = "code", alternate = {"error_code"})
    public String code;

    @SerializedName(value = "processed_image_url", alternate = {"image_url", "url", "result_url"})
    public String processedImageUrl;

    @SerializedName("quota")
    public AiQuota quota;

    @SerializedName("data")
    public AiProcessResponse data;

    public boolean isSuccess() {
        AiProcessResponse body = data != null ? data : this;
        if (body.successRaw == null) {
            return body.processedImageUrl != null && !body.processedImageUrl.isEmpty();
        }
        return AiConfig.isTruthy(body.successRaw);
    }

    public String resolvedUrl() {
        if (data != null && data.processedImageUrl != null) {
            return data.processedImageUrl;
        }
        return processedImageUrl;
    }

    public String resolvedMessage() {
        if (data != null && data.message != null) {
            return data.message;
        }
        return message;
    }

    public String resolvedCode() {
        if (data != null && data.code != null) {
            return data.code;
        }
        return code;
    }

    public AiQuota resolvedQuota() {
        if (data != null && data.quota != null) {
            return data.quota;
        }
        return quota;
    }
}
