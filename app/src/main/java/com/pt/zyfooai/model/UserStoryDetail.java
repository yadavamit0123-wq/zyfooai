package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserStoryDetail {

    @SerializedName(value = "id", alternate = {"storyId", "story_id"})
    public String id;

    @SerializedName(value = "userId", alternate = {"user_id"})
    public String userId;

    @SerializedName(value = "userName", alternate = {"user_name", "name"})
    public String userName;

    @SerializedName(value = "userAvatar", alternate = {"user_avatar", "avatar"})
    public String userAvatar;

    @SerializedName(value = "createdAt", alternate = {"created_at"})
    public String createdAt;

    @SerializedName(value = "expiresAt", alternate = {"expires_at"})
    public String expiresAt;

    @SerializedName("media")
    public List<UserStoryMedia> media;
}
