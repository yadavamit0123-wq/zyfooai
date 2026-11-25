package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class ReferralListItem {

    @SerializedName("name")
    public String name;

    @SerializedName(value = "initial_or_avatar", alternate = {"avatar", "initial", "image"})
    public String initialOrAvatar;

    @SerializedName(value = "date", alternate = {"created_at", "createdAt", "datetime", "date_time"})
    public String date;

    /** Optional earning credited for this referral (when API sends it). */
    @SerializedName(value = "amount", alternate = {"earning", "credit", "wallet_credit", "referrer_amount"})
    public String amount;
}
