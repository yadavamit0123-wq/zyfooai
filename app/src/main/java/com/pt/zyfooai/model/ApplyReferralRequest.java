package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class ApplyReferralRequest {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("referral_code")
    public String referralCode;

    @SerializedName("device_id")
    public String deviceId;

    public ApplyReferralRequest(String userId, String referralCode, String deviceId) {
        this.userId = userId;
        this.referralCode = referralCode;
        this.deviceId = deviceId;
    }
}
