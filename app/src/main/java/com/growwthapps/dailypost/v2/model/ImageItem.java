package com.growwthapps.dailypost.v2.model;

import com.google.gson.annotations.SerializedName;

public class ImageItem {
    @SerializedName("url")
    private String imageUrl;

    @SerializedName("title")
    private String title;

    // Add other necessary fields

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }
}
