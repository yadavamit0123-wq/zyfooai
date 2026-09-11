package com.growwthapps.dailypost.v2.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class FestivalItem {

    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String name;

    @SerializedName("image")
    public String image;

    @SerializedName("festival_date")
    public String festivalsDate;



    @SerializedName("date")
    public String date;


    @SerializedName("day")
    public String day;


    @SerializedName("month")
    public String month;




    @SerializedName("status")
    public boolean isActive;

    @SerializedName("video")
    public boolean video;


    public FestivalItem(@NonNull String id, String name, String image, String festivalsDate, boolean isActive, boolean video) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.festivalsDate = festivalsDate;
        this.isActive = isActive;
        this.video = video;
    }


}

