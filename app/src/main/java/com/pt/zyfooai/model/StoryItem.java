package com.pt.zyfooai.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class StoryItem {

    @NonNull
    @SerializedName("storyId")
    public String storyId;

    @SerializedName("title")
    public String title;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("id")
    public String festivalId;

    @SerializedName("storyType")
    public String type;

    @SerializedName("date")
    public String date;

    @SerializedName("externalLink")
    public String externalLink;

    @SerializedName("video")
    public boolean video;

    public StoryItem(@NonNull String storyId, String title, String imageUrl, String festivalId, String type, String externalLink, boolean video) {
        this.storyId = storyId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.festivalId = festivalId;
        this.type = type;
        this.externalLink = externalLink;
        this.video = video;
    }


}
