package com.growwthapps.dailypost.v2.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserItem implements Serializable {

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @NonNull
    @SerializedName("user_id")
    public String userId;

    @SerializedName("name")
    public String userName;

    @SerializedName("image")
    public String userImage;

    @SerializedName("email")
    public String email;

    @SerializedName("user_status")
    public String user_status;

    @SerializedName("login_type")
    public String login_type;

    @SerializedName("mobile_no")
    public String phone;

    @SerializedName("designation")
    public String designation;

    @SerializedName("user_address")
    public String address;

    @SerializedName("user_instagram")
    public String instagram;

    @SerializedName("user_facebook")
    public String facebook;

    @SerializedName("plan_id")
    public String planId;
    @SerializedName("plan_name")
    public String planName;

    @SerializedName("plan_start_date")
    public String planStartDate;

    @SerializedName("plan_end_date")
    public String planEndDate;

    @SerializedName("default_type")
    public String defaultType;

    @SerializedName("business_id")
    public String businessID;

    @SerializedName("political_id")
    public String politicalID;

    @SerializedName("business_image")
    public String businessImage;

    @SerializedName("business_name")
    public String businessName;

    @SerializedName("business_number")
    public String businessNumber;

    @SerializedName("business_instagram")
    public String businessInstagram;

    @SerializedName("business_facebook")
    public String businessfacebook;

    @SerializedName("business_whatsapp")
    public String businessWhatsapp;

    @SerializedName("business_address")
    public String businessAddress;

    @SerializedName("business_detail")
    public String businessDetail;

    @SerializedName("business_email")
    public String businessEmail;

    @SerializedName("business_website")
    public String businessWebsite;

    public String getLogin_type() {
        return login_type;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(String businessImage) {
        this.businessImage = businessImage;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getBusinessInstagram() {
        return businessInstagram;
    }

    public void setBusinessInstagram(String businessInstagram) {
        this.businessInstagram = businessInstagram;
    }

    public String getBusinessWhatsapp() {
        return businessWhatsapp;
    }

    public void setBusinessWhatsapp(String businessWhatsapp) {
        this.businessWhatsapp = businessWhatsapp;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessDetail() {
        return businessDetail;
    }

    public void setBusinessDetail(String businessDetail) {
        this.businessDetail = businessDetail;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessWebsite() {
        return businessWebsite;
    }

    public void setBusinessWebsite(String businessWebsite) {
        this.businessWebsite = businessWebsite;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }


    public String getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(String planStartDate) {
        this.planStartDate = planStartDate;
    }

    public String getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(String planEndDate) {
        this.planEndDate = planEndDate;
    }


}
