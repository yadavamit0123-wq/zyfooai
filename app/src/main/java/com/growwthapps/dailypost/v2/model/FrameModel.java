package com.growwthapps.dailypost.v2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "frameModel")

public class FrameModel {

    @PrimaryKey(autoGenerate = true)
    public int frame_id_auto;
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("thumbnail")
    private String thumbnail;
    private String json;
    @SerializedName("zip")
    private String frame_zip;

    @ColumnInfo(name = "layout")
    public int layout;

    @ColumnInfo(name = "previewImage")
    public int previewImage;
    @SerializedName("premium")
    private boolean is_premium;
    @SerializedName("frame_category")
    private String frame_category;
    @SerializedName("frame_type")
    private String type;


    public FrameModel(String id, String title, String thumbnail, String json, String frame_zip, int layout, int previewImage, boolean is_premium, String frame_category, String type) {
        this.id = id;
        this.title = title;
        this.thumbnail = thumbnail;
        this.json = json;
        this.frame_zip = frame_zip;
        this.layout = layout;
        this.previewImage = previewImage;
        this.is_premium = is_premium;
        this.frame_category = frame_category;
        this.type = type;
    }

    public FrameModel() {

    }



    public int getFrame_id_auto() {
        return frame_id_auto;
    }

    public void setFrame_id_auto(int frame_id_auto) {
        this.frame_id_auto = frame_id_auto;
    }

    public String getFrame_zip() {
        return frame_zip;
    }

    public void setFrame_zip(String frame_zip) {
        this.frame_zip = frame_zip;
    }

    public boolean isIs_premium() {
        return is_premium;
    }

    public void setIs_premium(boolean is_premium) {
        this.is_premium = is_premium;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }


    public int getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(int previewImage) {
        this.previewImage = previewImage;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }


    public String getFrame_category() {
        return frame_category;
    }

    public void setFrame_category(String frame_category) {
        this.frame_category = frame_category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
