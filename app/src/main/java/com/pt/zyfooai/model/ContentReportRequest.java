package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class ContentReportRequest {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("post_id")
    public String postId;

    @SerializedName("post_type")
    public String postType;

    @SerializedName("email")
    public String email;

    @SerializedName("reason")
    public String reason;

    @SerializedName("post_thumbnail_url")
    public String postThumbnailUrl;

    @SerializedName("frame_id")
    public String frameId;

    public ContentReportRequest(
            String userId,
            String postId,
            String postType,
            String email,
            String reason,
            String postThumbnailUrl,
            String frameId
    ) {
        this.userId = userId;
        this.postId = postId;
        this.postType = postType;
        this.email = email;
        this.reason = reason;
        this.postThumbnailUrl = postThumbnailUrl;
        this.frameId = frameId;
    }
}
