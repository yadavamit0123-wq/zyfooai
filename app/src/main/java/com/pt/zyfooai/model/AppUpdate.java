package com.pt.zyfooai.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class AppUpdate {

    @SerializedName("new_app_version_code")
    public String newAppVersionCode;

    @SerializedName("update_popup_show")
    public String updatePopupShow;

    @SerializedName("app_link")
    public String appLink;

    @SerializedName("cancel_option")
    public String cancelOption;

    @SerializedName("description")
    public String versionMessage;

    @NonNull
    public String getNewAppVersionCode() {
        return newAppVersionCode;
    }

    public void setNewAppVersionCode(@NonNull String newAppVersionCode) {
        this.newAppVersionCode = newAppVersionCode;
    }

    public String getUpdatePopupShow() {
        return updatePopupShow;
    }

    public void setUpdatePopupShow(String updatePopupShow) {
        this.updatePopupShow = updatePopupShow;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public String getCancelOption() {
        return cancelOption;
    }

    public void setCancelOption(String cancelOption) {
        this.cancelOption = cancelOption;
    }

    public String getVersionMessage() {
        return versionMessage;
    }

    public void setVersionMessage(String versionMessage) {
        this.versionMessage = versionMessage;
    }
}
