package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class UserStoryDetailResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("data")
    public UserStoryDetail data;

    @SerializedName("code")
    public String code;

    @SerializedName("message")
    public String message;
}
