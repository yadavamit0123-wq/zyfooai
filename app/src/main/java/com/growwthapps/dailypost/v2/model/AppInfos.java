package com.growwthapps.dailypost.v2.model;

import com.google.gson.annotations.SerializedName;

public class AppInfos {

    @SerializedName("AppInfo")
    public AppInfo appInfo;


    @SerializedName("AdsModel")
    public AdsModel adsModel;

    @SerializedName("user")
    public UserItem userItem;

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
}
