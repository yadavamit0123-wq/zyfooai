package com.growwthapps.dailypost.v2.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeatureItem {



    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String title;

    @SerializedName("icon")
    public String image;

    @SerializedName("posts")
    public List<PostItem> postItemList;

    public FeatureItem(@NonNull String id,String title, String image,List<PostItem> postItemList) {
        this.id = id;

        this.title = title;
        this.image = image;

        this.postItemList = postItemList;
    }
}
