package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AiConversation {

    @SerializedName(value = "id", alternate = {"conversation_id", "conversationId"})
    public String id;

    @SerializedName(value = "title", alternate = {"name", "preview"})
    public String title;

    @SerializedName(value = "provider", alternate = {"model_provider"})
    public String provider;

    @SerializedName(value = "updated_at", alternate = {"updatedAt", "created_at", "createdAt"})
    public String updatedAt;

    @SerializedName("messages")
    public List<AiChatMessage> messages;
}
