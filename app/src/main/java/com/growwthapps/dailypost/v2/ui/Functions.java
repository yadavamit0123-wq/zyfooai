package com.growwthapps.dailypost.v2.ui;

import android.content.Context;
import android.widget.Toast;

import com.growwthapps.dailypost.v2.api.ApiClient;
import com.growwthapps.dailypost.v2.model.UserItem;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;

public class Functions {

    public static void saveUserData(Context context, UserItem userItem) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setString(Constant.USER_ID, userItem.userId);
        preferenceManager.setString(Constant.USER_NAME, userItem.userName);
        preferenceManager.setString(Constant.USER_PHONE, userItem.phone);
        preferenceManager.setString(Constant.USER_DESIGNATION, userItem.designation);
        preferenceManager.setString(Constant.USER_ADDRESS, userItem.address);
        preferenceManager.setString(Constant.USER_INSTAGRAM, userItem.instagram);
        preferenceManager.setString(Constant.USER_FACEBOOK, userItem.facebook);
        preferenceManager.setString(Constant.USER_EMAIL, userItem.email);
        preferenceManager.setString(Constant.USER_IMAGE, ApiClient.App_URl+userItem.userImage);
        preferenceManager.setString(Constant.PLAN_ID, userItem.planId);
        preferenceManager.setString(Constant.PLAN_NAME, userItem.planName);
        preferenceManager.setString(Constant.PLAN_START_DATE, userItem.planStartDate);
        preferenceManager.setString(Constant.PLAN_END_DATE, userItem.planEndDate);
        preferenceManager.setString(Constant.DEFAULT_TYPE, userItem.defaultType);
        preferenceManager.setString(Constant.BUSINESS_ID, userItem.businessID);
        preferenceManager.setString(Constant.POLITICAL_ID, userItem.politicalID);
        preferenceManager.setString(Constant.BUSINESS_IMAGE, ApiClient.App_URl+userItem.businessImage);
        preferenceManager.setString(Constant.BUSINESS_NAME, userItem.businessName);
        preferenceManager.setString(Constant.BUSINESS_NUMBER, userItem.businessNumber);
        preferenceManager.setString(Constant.BUSINESS_INSTAGRAM, userItem.businessInstagram);
        preferenceManager.setString(Constant.BUSINESS_FACEBOOK, userItem.businessfacebook);
        preferenceManager.setString(Constant.BUSINESS_WHATSAPP, userItem.businessWhatsapp);
        preferenceManager.setString(Constant.BUSINESS_ADDRESS, userItem.businessAddress);
        preferenceManager.setString(Constant.BUSINESS_DETAIL, userItem.businessDetail);
        preferenceManager.setString(Constant.BUSINESS_EMAIL, userItem.businessEmail);
        preferenceManager.setString(Constant.BUSINESS_WEBSITE, userItem.businessWebsite);
    }
}
