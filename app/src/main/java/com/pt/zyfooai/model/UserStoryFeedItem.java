package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

/**
 * One ring on the home Stories row ({@code GET /api/user-stories}).
 */
public class UserStoryFeedItem {

    @SerializedName(value = "storyId", alternate = {"id", "story_id"})
    public String storyId;

    @SerializedName(value = "userId", alternate = {"user_id"})
    public String userId;

    @SerializedName(value = "userName", alternate = {"user_name", "name"})
    public String userName;

    @SerializedName(value = "userAvatar", alternate = {"user_avatar", "avatar", "profile"})
    public String userAvatar;

    @SerializedName(value = "createdAt", alternate = {"created_at"})
    public String createdAt;

    @SerializedName(value = "expiresAt", alternate = {"expires_at"})
    public String expiresAt;

    @SerializedName("seen")
    public boolean seen;

    @SerializedName(value = "mediaCount", alternate = {"media_count"})
    public int mediaCount;

    @SerializedName(value = "thumbnailUrl", alternate = {"thumbnail_url", "thumbnail"})
    public String thumbnailUrl;

    /** Local-only: first cell is "Add your story". */
    public transient boolean isAddButton;
}
