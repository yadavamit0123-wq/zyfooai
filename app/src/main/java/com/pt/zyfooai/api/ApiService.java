package com.pt.zyfooai.api;


import com.pt.zyfooai.model.AppInfos;
import com.pt.zyfooai.model.ApplyReferralRequest;
import com.pt.zyfooai.model.ApplyReferralResponse;
import com.pt.zyfooai.model.CategoryItem;
import com.pt.zyfooai.model.AiChatResponse;
import com.pt.zyfooai.model.AiConfigResponse;
import com.pt.zyfooai.model.AiConversationDetailResponse;
import com.pt.zyfooai.model.AiConversationListResponse;
import com.pt.zyfooai.model.AiImageResponse;
import com.pt.zyfooai.model.AiProcessResponse;
import com.pt.zyfooai.model.CheckReferralResponse;
import com.pt.zyfooai.model.ContentReportRequest;
import com.pt.zyfooai.model.ContentReportResponse;
import com.pt.zyfooai.model.FaqListResponse;
import com.pt.zyfooai.model.FrameListResponse;
import com.pt.zyfooai.model.LanguageItem;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.model.ReferralListResponse;
import com.pt.zyfooai.model.ReferralMeResponse;
import com.pt.zyfooai.model.StoryItem;
import com.pt.zyfooai.model.SubscriptionModel;
import com.pt.zyfooai.model.UserItem;
import com.pt.zyfooai.model.UserStoryDetailResponse;
import com.pt.zyfooai.model.UserStoryListResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
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

    @GET("api/frames")
    Call<FrameListResponse> getFrames(
            @Query("mediaType") String mediaType,
            @Query("status") String status
    );

    @GET("api/frames/sync-meta")
    Call<FrameListResponse> getFramesSyncMeta();

    // --- User Stories (24h, all logged-in users) — separate from getFestival ---

    @GET("api/user-stories")
    Call<UserStoryListResponse> getUserStories(@Query("user_id") String userId);

    @GET("api/user-stories/{storyId}")
    Call<UserStoryDetailResponse> getUserStory(@Path("storyId") String storyId);

    @Multipart
    @POST("api/user-stories")
    Call<UserStoryDetailResponse> createUserStory(
            @Part("user_id") RequestBody userId,
            @Part List<MultipartBody.Part> images
    );

    @POST("api/user-stories/{storyId}/view")
    Call<UserStoryDetailResponse> markUserStorySeen(@Path("storyId") String storyId);

    @DELETE("api/user-stories/{storyId}")
    Call<UserStoryDetailResponse> deleteUserStory(@Path("storyId") String storyId);

    // --- Refer & Earn (server is source of truth) ---

    @GET("api/check-reffercode/{code}")
    Call<CheckReferralResponse> checkReferralCode(
            @Path("code") String code,
            @Query("user_id") String userId
    );

    @POST("api/apply-reffer")
    Call<ApplyReferralResponse> applyReferral(@Body ApplyReferralRequest body);

    @GET("api/referral/me")
    Call<ReferralMeResponse> getReferralMe(@Query("user_id") String userId);

    @GET("api/referral/list")
    Call<ReferralListResponse> getReferralList(
            @Query("user_id") String userId,
            @Query("page") int page
    );

    @GET("api/faq/{category}")
    Call<FaqListResponse> getFaq(@Path("category") String category);

    // --- Content Report (admin Content Reports panel) ---

    @POST("api/content-report")
    Call<ContentReportResponse> submitContentReport(@Body ContentReportRequest body);

    // --- AI (admin AI Settings) ---

    @GET("api/ai/config")
    Call<AiConfigResponse> getAiConfig(@Query("user_id") String userId);

    @Multipart
    @POST("api/ai/chat")
    Call<AiChatResponse> aiChat(@PartMap Map<String, RequestBody> fields);

    @Multipart
    @POST("api/ai/chat")
    Call<AiChatResponse> aiChatWithImage(
            @PartMap Map<String, RequestBody> fields,
            @Part MultipartBody.Part image
    );

    @POST("api/ai/image")
    Call<AiImageResponse> aiCreateImage(@Body Map<String, String> body);

    @Multipart
    @POST("api/ai/process")
    Call<AiProcessResponse> aiProcess(@PartMap Map<String, RequestBody> fields);

    @Multipart
    @POST("api/ai/process")
    Call<AiProcessResponse> aiProcessWithImage(
            @PartMap Map<String, RequestBody> fields,
            @Part MultipartBody.Part image
    );

    @GET("api/ai/conversations")
    Call<AiConversationListResponse> aiConversations(@Query("user_id") String userId);

    @GET("api/ai/conversations/{id}")
    Call<AiConversationDetailResponse> aiConversation(
            @Path("id") String id,
            @Query("user_id") String userId
    );

    @DELETE("api/ai/conversations/{id}")
    Call<AiChatResponse> deleteAiConversation(
            @Path("id") String id,
            @Query("user_id") String userId
    );

}
