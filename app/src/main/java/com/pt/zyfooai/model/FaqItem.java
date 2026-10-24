package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class FaqItem {

    @SerializedName(value = "question", alternate = {"title", "q"})
    public String question;

    @SerializedName(value = "answer", alternate = {"content", "a", "description"})
    public String answer;

    public transient boolean expanded;
}
