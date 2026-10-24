package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqListResponse {

    @SerializedName("success")
    public Object successRaw;

    @SerializedName(value = "data", alternate = {"faqs", "questions"})
    public List<FaqItem> data;

    @SerializedName("message")
    public String message;
}
