package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AiImageResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName("message")
    public String message;

    @SerializedName(value = "code", alternate = {"error_code"})
    public String code;

    @SerializedName(value = "image_url", alternate = {"url", "processed_image_url", "result_url"})
    public String imageUrl;

    @SerializedName(value = "conversation_id", alternate = {"conversationId"})
    public String conversationId;

    @SerializedName("quota")
    public AiQuota quota;

    @SerializedName("data")
    public AiImageResponse data;

    public boolean isSuccess() {
        AiImageResponse body = data != null ? data : this;
        if (body.successRaw == null) {
            return body.imageUrl != null && !body.imageUrl.isEmpty();
        }
        return AiConfig.isTruthy(body.successRaw);
    }

    public String resolvedImageUrl() {
        if (data != null && data.imageUrl != null) {
            return data.imageUrl;
        }
        return imageUrl;
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
