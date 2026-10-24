package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AiChatMessage {

    @SerializedName(value = "role", alternate = {"sender", "type"})
    public String role;

    @SerializedName(value = "content", alternate = {"message", "text", "reply"})
    public String content;

    @SerializedName(value = "image_url", alternate = {"imageUrl", "url"})
    public String imageUrl;

    /** Local UI helper */
    public transient boolean isMine;

    public static AiChatMessage user(String text) {
        AiChatMessage m = new AiChatMessage();
        m.role = "user";
        m.content = text;
        m.isMine = true;
        return m;
    }

    public static AiChatMessage assistant(String text) {
        AiChatMessage m = new AiChatMessage();
        m.role = "assistant";
        m.content = text;
        m.isMine = false;
        return m;
    }

    public boolean resolveIsMine() {
        if (isMine) {
            return true;
        }
        if (role == null) {
            return false;
        }
        String r = role.trim().toLowerCase();
        return "user".equals(r) || "me".equals(r) || "human".equals(r);
    }
}
