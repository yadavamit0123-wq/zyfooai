package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserStoryListResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("data")
    public List<UserStoryFeedItem> data;

    @SerializedName("message")
    public String message;
}
