package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class UserStoryMedia {

    @SerializedName(value = "id", alternate = {"media_id"})
    public String id;

    @SerializedName(value = "url", alternate = {"media_url", "image"})
    public String url;

    @SerializedName(value = "sortOrder", alternate = {"sort_order"})
    public int sortOrder;
}
