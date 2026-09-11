package com.growwthapps.dailypost.v2.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class CategoryItems {

    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("icon")
    public String image;

    @SerializedName("category_id")
    public String category_id;

    @SerializedName("video")
    public boolean video;


    public CategoryItems(@NonNull String id, String name, String image, boolean video) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.video = video;
    }

    public CategoryItems() {

    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

}

