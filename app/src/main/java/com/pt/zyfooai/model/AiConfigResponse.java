package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AiConfigResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName(value = "data", alternate = {"AI", "ai", "config"})
    public AiConfig data;

    /** Some backends return config at root. */
    @SerializedName("ai_enabled")
    public Object aiEnabledRaw;

    @SerializedName("message")
    public String message;

    public AiConfig resolved() {
        if (data != null) {
            return data;
        }
        if (aiEnabledRaw != null) {
            AiConfig cfg = new AiConfig();
            cfg.aiEnabledRaw = aiEnabledRaw;
            return cfg;
        }
        return null;
    }
}
