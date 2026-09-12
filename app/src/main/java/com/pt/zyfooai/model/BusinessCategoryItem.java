package com.pt.zyfooai.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class BusinessCategoryItem {

    @NonNull
    @SerializedName("id")
    public String businessCategoryId;

    @SerializedName("name")
    public String businessCategoryName;

    @SerializedName("icon")
    public String businessCategoryIcon;

    @SerializedName("video")
    public boolean video;

    public BusinessCategoryItem(@NonNull String businessCategoryId, String businessCategoryName, String businessCategoryIcon, boolean video) {
        this.businessCategoryId = businessCategoryId;
        this.businessCategoryName = businessCategoryName;
        this.businessCategoryIcon = businessCategoryIcon;
        this.video = video;
    }
}
