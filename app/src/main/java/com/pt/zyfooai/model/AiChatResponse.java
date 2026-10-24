package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AiChatResponse {

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    @SerializedName("message")
    public String message;

    @SerializedName(value = "code", alternate = {"error_code", "errorCode"})
    public String code;

    @SerializedName(value = "reply", alternate = {"response", "answer", "content", "assistant_message"})
    public String reply;

    @SerializedName(value = "conversation_id", alternate = {"conversationId", "id"})
    public String conversationId;

    @SerializedName("quota")
    public AiQuota quota;

    @SerializedName("data")
    public AiChatResponse data;

    public boolean isSuccess() {
        AiChatResponse body = data != null ? data : this;
        if (body.successRaw == null) {
            return body.reply != null && !body.reply.isEmpty();
        }
        return AiConfig.isTruthy(body.successRaw);
    }

    public String resolvedReply() {
        if (data != null && data.reply != null) {
            return data.reply;
        }
        return reply;
    }

    public String resolvedConversationId() {
        if (data != null && data.conversationId != null) {
            return data.conversationId;
        }
        return conversationId;
    }

    public String resolvedCode() {
        if (data != null && data.code != null) {
            return data.code;
        }
        return code;
    }

    public String resolvedMessage() {
        if (data != null && data.message != null) {
            return data.message;
        }
        return message;
    }

    public AiQuota resolvedQuota() {
        if (data != null && data.quota != null) {
            return data.quota;
        }
        return quota;
    }
}
