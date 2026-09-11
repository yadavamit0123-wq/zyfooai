package com.growwthapps.dailypost.v2.api;


import com.growwthapps.dailypost.v2.model.AppInfos;

import com.growwthapps.dailypost.v2.model.CategoryItem;

import com.growwthapps.dailypost.v2.model.LanguageItem;
import com.growwthapps.dailypost.v2.model.PostItem;

import com.growwthapps.dailypost.v2.model.StoryItem;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.model.UserItem;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @GET("loginUser")
    Call<UserItem> login(@Query("login_type") String login_type,
                         @Query("name") String name,
                         @Query("email") String email,
                         @Query("profile") String profile,
                         @Query("number") String number);

    @Multipart
    @POST("api/updateBusiness")
    Call<UserItem> updateBusiness(
            @Part("user_id") RequestBody user_id,
            @Part("business_name") RequestBody businessName,
            @Part("business_address") RequestBody businessAddress,
            @Part("business_detail") RequestBody businessDetail,
            @Part("business_whatsapp") RequestBody businessWhatsapp,
            @Part("business_instagram") RequestBody businessInstagram,
            @Part MultipartBody.Part businessLogo
    );

    @Multipart
    @POST("api/updateProfile")
    Call<UserItem> updateProfile(
            @Part("user_id") RequestBody user_id,
            @Part("name") RequestBody name,
            @Part("designation") RequestBody designation,
            @Part("number") RequestBody number,
            @Part("instagram") RequestBody instagram,
            @Part("facebook") RequestBody facebook,
            @Part MultipartBody.Part logo
    );

    @Multipart
    @POST("api/updateBusinessProfile")
    Call<UserItem> updateProfile(
            @Part("user_id") RequestBody user_id,
            @Part("name") RequestBody name,
            @Part("designation") RequestBody designation,
            @Part("number") RequestBody number,
            @Part("instagram") RequestBody instagram,
            @Part("facebook") RequestBody facebook,
            @Part("default_type") RequestBody default_type,
            @Part MultipartBody.Part logo
    );

    @Multipart
    @POST("api/updateBusinessProfile")
    Call<UserItem> updateBusinessProfile(
            @Part("user_id") RequestBody user_id,
            @Part("name") RequestBody name,
            @Part("political_id") RequestBody political_id,
            @Part("business_id") RequestBody business_id,
            @Part("business_name") RequestBody business_name,
            @Part("business_email") RequestBody business_email,
            @Part("business_designation") RequestBody business_designation,
            @Part("designation") RequestBody designation,
            @Part("business_number") RequestBody number,
            @Part("business_website") RequestBody website,
            @Part("business_instagram") RequestBody instagram,
            @Part("business_facebook") RequestBody facebook,
            @Part("default_type") RequestBody default_type,
            @Part("business_address") RequestBody business_address,
            @Part MultipartBody.Part logo,
            @Part MultipartBody.Part business_logo
    );


    @GET("ContactUs")
    Call<ApiStatus> contactUsMessage(@Query("user_id") String userid,
                                     @Query("name") String name,
                                     @Query("email") String email,
                                     @Query("mobile_no") String number,
                                     @Query("message") String massage);

    @GET("getCategory")
    Call<List<CategoryItem>> getCategories(@Query("type") String type);

    @GET("getBusinessCategory")
    Call<List<CategoryItem>> getBusinessCategory(@Query("search") String search,@Query("type") String type);

    @GET("getBackgroundCategory")
    Call<List<CategoryItem>> getBackgroundCategory();

    @GET("getStickerCategory")
    Call<List<CategoryItem>> getStickerCategory();

    @GET("getMusicCategory")
    Call<List<CategoryItem>> getMusicCategory();

    @GET("getAllLanguages")
    Call<List<LanguageItem>> getLanguagess();

    @GET("getSubscriptionPlan")
    Call<List<SubscriptionModel>> getSubscriptionPlan();


    //*** Get App Info****
    @GET("dailyPost")
    Call<List<PostItem>> getDailyPostData(@Query("page") Integer page, @Query("catId") String categoryID, @Query("language") String language, @Query("business_id") String business_id,
                                          @Query("political_id") String political_id);


    //*** Get App Info****
    @GET("getAppInfo")
    Call<AppInfos> getAppInfo(@Query("user_id") String userID);

    @GET("getFestival")
    Call<List<StoryItem>> getFestival();

    @GET("getFestivalPost")
    Call<List<PostItem>> getFestivalPost(@Query("page") Integer page, @Query("festival_id") String categoryID, @Query("language_id") String language);

    @GET("getBackgroundByCategory")
    Call<List<PostItem>> getBackgroundByCategory(@Query("category_id") String categoryID);

    @GET("getStickersByCategory")
    Call<List<PostItem>> getStickersByCategory(@Query("category_id") String categoryID);

    @GET("getMusicByCategory")
    Call<List<PostItem>> getMusicByCategory(@Query("category_id") String categoryID);


    @GET("authDestroy")
    Call<List<UserItem>> authDestroy(@Query("authId") String userId);


    @GET("storeTransaction")
    Call<UserItem> storeTransaction(@Query("user_id") String user_id,
                                    @Query("plan_id") String plan_id,
                                    @Query("amount") String amount,
                                    @Query("transaction_id") String transaction_id);

    @GET("storeDevice")
    Call<SubscriptionModel> storeDevice(@Query("device_id") String code);

}
