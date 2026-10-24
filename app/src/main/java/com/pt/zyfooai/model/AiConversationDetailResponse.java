package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AiConversationDetailResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName(value = "data", alternate = {"conversation"})
    public AiConversation data;

    @SerializedName("message")
    public String message;
}
