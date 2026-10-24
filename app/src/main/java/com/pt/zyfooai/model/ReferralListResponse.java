package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReferralListResponse {

    @SerializedName("success")
    public Object successRaw;

    @SerializedName(value = "data", alternate = {"referrals", "list"})
    public List<ReferralListItem> data;

    @SerializedName("message")
    public String message;
}
