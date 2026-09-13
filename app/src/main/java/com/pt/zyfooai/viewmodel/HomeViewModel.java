package com.pt.zyfooai.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.pt.zyfooai.model.AppInfos;
import com.pt.zyfooai.model.CategoryItem;

import com.pt.zyfooai.model.LanguageItem;
import com.pt.zyfooai.model.PostItem;

import com.pt.zyfooai.model.StoryItem;

import com.pt.zyfooai.model.SubscriptionModel;
import com.pt.zyfooai.model.UserItem;
import com.pt.zyfooai.respository.HomeRespository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private HomeRespository respository;
    private final MutableLiveData<List<PostItem>> dailyPostsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryItem>> categoriesLiveData = new MutableLiveData<>();

    public HomeViewModel() {
        respository = new HomeRespository();
    }

    public LiveData<List<PostItem>> observeDailyPosts() {
        return dailyPostsLiveData;
    }

    public void loadDailyPosts(int page, String catid, String language, String business_id, String political_id) {
        respository.loadDailyPosts(page, catid, language, business_id, political_id, dailyPostsLiveData);
    }

    public LiveData<List<CategoryItem>> observeCategories() {
        return categoriesLiveData;
    }

    public void loadCategories(String type) {
        respository.loadCategories(type, categoriesLiveData);
    }

    public LiveData<UserItem> login(String loginType, String displayName, String email, String photoUrl, String phoneNumber) {

        return respository.login(loginType,displayName,email,photoUrl,phoneNumber);
    }

    public LiveData<List<PostItem>> getDailyPosts(int page, String catid, String language, String business_id, String political_id) {
        return respository.getDailyPosts(page, catid, language, business_id,political_id);
    }
    public LiveData<List<PostItem>> getFestivalPost(int page, String catid, String language) {
        return respository.getFestivalPost(page, catid, language);
    }

    public LiveData<List<PostItem>> getBackgroundByCategory(String catid) {
        return respository.getBackgroundByCategory(catid);
    }

    public LiveData<List<PostItem>> getStickerByCategory(String catid) {
        return respository.getStickerByCategory(catid);
    }

    public LiveData<List<PostItem>> getMusicByCategory(String catid) {
        return respository.getMusicByCategory(catid);
    }

    public LiveData<List<CategoryItem>> getCategories(String type) {

        return respository.getCategories(type);
    }

    public LiveData<List<CategoryItem>> getBusinessCategory(String search, String type) {

        return respository.getBusinessCategory(search,type);
    }

    public LiveData<List<SubscriptionModel>> getSubscriptionPlan() {
        return respository.getSubscriptionPlan();
    }

    public LiveData<List<CategoryItem>> getBackgroundCategory() {

        return respository.getBackgroundCategory();
    }

    public LiveData<List<CategoryItem>> getStickerCategory() {

        return respository.getStickerCategory();
    }

    public LiveData<List<CategoryItem>> getMusicCategory() {

        return respository.getMusicCategory();
    }

    public LiveData<List<LanguageItem>> getLanguagess() {

        return respository.getLanguagess();
    }

    public LiveData<AppInfos> getAppInfo(String userID) {

        return respository.getAppInfo(userID);
    }

    public LiveData<SubscriptionModel> storeDevice(String device) {
        return respository.storeDevice(device);
    }

    public LiveData<List<StoryItem>> getFestival() {
        return respository.getFestival();
    }

    public LiveData<UserItem> updateBusiness(String user_id, String businessName, String businessAddress, String businessDetails, String businessWhatsapp, String businessInstagram, String businessLogo) {
        return respository.updateBusiness(user_id, businessName, businessAddress, businessDetails, businessWhatsapp, businessInstagram, businessLogo);
    }

    public LiveData<UserItem> updateProfile(String user_id, String name, String designation, String number, String instagram,String facebook, String profile) {
        return respository.updateProfile(user_id, name, designation, number, instagram, facebook, profile);
    }

    public LiveData<UserItem> updateProfile(String user_id, String name, String designation, String number, String instagram,String facebook, String profile,String defaultType) {
        return respository.updateProfile(user_id, name, designation, number, instagram, facebook, profile,defaultType);
    }

    public LiveData<UserItem> updateBusinessProfile(String user_id, String name, String politicalID, String businessID ,String businessname, String businessemail, String businessdesignation, String designation, String number, String website, String instagram, String facebook, String defaultType,String businessAddress, String userImageUrl, String businessImageUrl) {
        return respository.updateBusinessProfile(user_id, name,politicalID,businessID, businessname, businessemail, businessdesignation, designation, number, website, instagram, facebook, defaultType, businessAddress, userImageUrl, businessImageUrl);
    }
}
