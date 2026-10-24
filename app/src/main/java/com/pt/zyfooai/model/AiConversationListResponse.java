package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AiConversationListResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName(value = "data", alternate = {"conversations", "items"})
    public List<AiConversation> data;

    @SerializedName("message")
    public String message;
}
