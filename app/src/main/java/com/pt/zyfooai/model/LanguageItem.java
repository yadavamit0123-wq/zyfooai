package com.pt.zyfooai.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class LanguageItem {

    @NonNull
    @SerializedName("id")
    public int id;
    public String image;
    @SerializedName("title")
    public String title;
    public boolean isChecked;

    public LanguageItem(@NonNull int id, String image, String title, boolean isChecked) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.isChecked = isChecked;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
