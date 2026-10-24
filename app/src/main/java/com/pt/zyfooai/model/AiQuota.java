package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AiQuota {

    @SerializedName(value = "remaining_chat", alternate = {"chat_remaining", "remainingChat"})
    public Integer remainingChat;

    @SerializedName(value = "remaining_image", alternate = {"image_remaining", "remainingImage"})
    public Integer remainingImage;

    @SerializedName(value = "remaining_process", alternate = {"process_remaining", "remainingProcess"})
    public Integer remainingProcess;

    @SerializedName(value = "daily_chat_limit", alternate = {"chat_limit"})
    public Integer dailyChatLimit;

    @SerializedName(value = "daily_image_limit", alternate = {"image_limit"})
    public Integer dailyImageLimit;

    @SerializedName(value = "package_active", alternate = {"has_package", "is_subscribed"})
    public Object packageActiveRaw;

    public boolean hasPackage() {
        return AiConfig.isTruthy(packageActiveRaw);
    }
}
