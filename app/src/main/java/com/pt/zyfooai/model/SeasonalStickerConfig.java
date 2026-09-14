package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

/**
 * Server-driven seasonal sticker pack (Firebase Remote Config JSON).
 *
 * Example:
 * {"enabled":true,"id":"diwali_2026","prompt":"Add yours: Diwali celebration 🪔",
 *  "start":"2026-10-25","end":"2026-11-10","force_dark_gold":true}
 */
public class SeasonalStickerConfig {

    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("id")
    private String id;

    @SerializedName("prompt")
    private String prompt;

    @SerializedName("start")
    private String start;

    @SerializedName("end")
    private String end;

    @SerializedName("force_dark_gold")
    private boolean forceDarkGold = true;

    public boolean isEnabled() {
        return enabled;
    }

    public String getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public boolean isForceDarkGold() {
        return forceDarkGold;
    }
}
