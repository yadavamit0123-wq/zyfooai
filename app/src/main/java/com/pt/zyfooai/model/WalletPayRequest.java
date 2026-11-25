package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class WalletPayRequest {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("plan_id")
    public String planId;

    @SerializedName("amount")
    public String amount;

    public WalletPayRequest(String userId, String planId, String amount) {
        this.userId = userId;
        this.planId = planId;
        this.amount = amount;
    }
}
