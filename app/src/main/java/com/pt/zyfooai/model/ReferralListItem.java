package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class ReferralListItem {

    @SerializedName("name")
    public String name;

    @SerializedName(value = "initial_or_avatar", alternate = {"avatar", "initial", "image"})
    public String initialOrAvatar;

    @SerializedName("date")
    public String date;
}
