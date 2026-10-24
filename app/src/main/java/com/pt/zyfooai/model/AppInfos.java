package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class AppInfos {


    @SerializedName("AppInfo")
    public AppInfo appInfo;


    @SerializedName("AdsModel")
    public AdsModel adsModel;

    @SerializedName("user")
    public UserItem userItem;

    /** Soft-extend: Refer & Earn admin config (optional; old APKs ignore). */
    @SerializedName(value = "ReferEarn", alternate = {"refer_earn", "referEarn", "Refer_Earn"})
    public ReferEarnConfig referEarn;

    /** Soft-extend: AI admin config (optional). */
    @SerializedName(value = "AI", alternate = {"ai", "Ai", "ai_config", "AiConfig"})
    public AiConfig ai;

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public AdsModel getAdsModel() {
        return adsModel;
    }

    public void setAdsModel(AdsModel adsModel) {
        this.adsModel = adsModel;
    }

    public UserItem getUserItem() {
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }

    public ReferEarnConfig getReferEarn() {
        return referEarn;
    }

    public void setReferEarn(ReferEarnConfig referEarn) {
        this.referEarn = referEarn;
    }
}
