package com.pt.zyfooai.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsModel {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("ads_status")
    @Expose
    private Integer adEnabled;

    @SerializedName("ads_banner_id")
    @Expose
    private String admobBanner;

    @SerializedName("ads_interstitial_id")
    @Expose
    private String admobInter;

    @SerializedName("ads_reward_id")
    @Expose
    private String admobReward;

    @SerializedName("ads_native_ads_id")
    @Expose
    private String admobNative;

    @SerializedName("ads_app_open_id")
    @Expose
    private String admobAppOpen;

    @SerializedName("ads_publisher_id")
    @Expose
    private String admobAppId;

    @SerializedName("ads_native_count")
    @Expose
    private Integer admobNativeCount;

    @SerializedName("ads_interstitial_count")
    @Expose
    private Integer admobInterCount;

    @SerializedName("ads_native_status")
    @Expose
    private Integer nativeAdsEnable;

    @SerializedName("ads_interstitial_status")
    @Expose
    private Integer interstitialAdsEnable;

    @SerializedName("ads_reward_status")
    @Expose
    private Integer rewardedAdsEnable;

    @SerializedName("ads_banner_status")
    @Expose
    private Integer bannerAdsEnable;

    @SerializedName("ads_app_open_status")
    @Expose
    private Integer appOpensAdsEnable;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdEnabled() {
        return adEnabled;
    }

    public void setAdEnabled(Integer adEnabled) {
        this.adEnabled = adEnabled;
    }

    public String getAdmobBanner() {
        return admobBanner;
    }

    public void setAdmobBanner(String admobBanner) {
        this.admobBanner = admobBanner;
    }

    public String getAdmobInter() {
        return admobInter;
    }

    public void setAdmobInter(String admobInter) {
        this.admobInter = admobInter;
    }

    public String getAdmobReward() {
        return admobReward;
    }

    public void setAdmobReward(String admobReward) {
        this.admobReward = admobReward;
    }

    public String getAdmobNative() {
        return admobNative;
    }

    public void setAdmobNative(String admobNative) {
        this.admobNative = admobNative;
    }

    public String getAdmobAppOpen() {
        return admobAppOpen;
    }

    public void setAdmobAppOpen(String admobAppOpen) {
        this.admobAppOpen = admobAppOpen;
    }

    public String getAdmobAppId() {
        return admobAppId;
    }

    public void setAdmobAppId(String admobAppId) {
        this.admobAppId = admobAppId;
    }

    public Integer getAdmobNativeCount() {
        return admobNativeCount;
    }

    public void setAdmobNativeCount(Integer admobNativeCount) {
        this.admobNativeCount = admobNativeCount;
    }

    public Integer getAdmobInterCount() {
        return admobInterCount;
    }

    public void setAdmobInterCount(Integer admobInterCount) {
        this.admobInterCount = admobInterCount;
    }

    public Integer getNativeAdsEnable() {
        return nativeAdsEnable;
    }

    public void setNativeAdsEnable(Integer nativeAdsEnable) {
        this.nativeAdsEnable = nativeAdsEnable;
    }

    public Integer getInterstitialAdsEnable() {
        return interstitialAdsEnable;
    }

    public void setInterstitialAdsEnable(Integer interstitialAdsEnable) {
        this.interstitialAdsEnable = interstitialAdsEnable;
    }

    public Integer getRewardedAdsEnable() {
        return rewardedAdsEnable;
    }

    public void setRewardedAdsEnable(Integer rewardedAdsEnable) {
        this.rewardedAdsEnable = rewardedAdsEnable;
    }

    public Integer getBannerAdsEnable() {
        return bannerAdsEnable;
    }

    public void setBannerAdsEnable(Integer bannerAdsEnable) {
        this.bannerAdsEnable = bannerAdsEnable;
    }

    public Integer getAppOpensAdsEnable() {
        return appOpensAdsEnable;
    }

    public void setAppOpensAdsEnable(Integer appOpensAdsEnable) {
        this.appOpensAdsEnable = appOpensAdsEnable;
    }

}