package com.pt.zyfooai.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pt.zyfooai.viewmodel.HomeViewModel;
import com.pt.zyfooai.viewmodel.UserViewModel;

public class Constant {

    public static final int BUSINESS_LOGO_WIDTH = 1;
    public static final int BUSINESS_LOGO_HEIGHT = 1;

    public static final String EXTERNAL = "external";

    public static final String API_KEY = "api_key";
    public static final String STATUS = "status";

    public static final String IS_LOGIN = "is_login";
    public static final String IS_SUBSCRIBE = "is_subscribe";
    public static final String LOAD_DATA = "load_data";
    public static final String DARK_MODE_ON = "dark_mode_on";

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PHONE = "user_phone";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_ADDRESS = "user_address";
    public static final String USER_DESIGNATION = "user_designation";
    public static final String USER_INSTAGRAM = "user_instagram";
    public static final String USER_FACEBOOK = "user_facebook";
    public static final String USER_LANGUAGE = "user_language";
    public static final String USER_SOCIAL_MEDIA_TYPE = "user_social_media_type";

    public static final String DEFAULT_TYPE = "default_type";
    public static final String BUSINESS_ID = "business_id";
    public static final String POLITICAL_ID = "political_id";

    public static final String BUSINESS_NAME = "business_name";
    public static final String BUSINESS_EMAIL = "business_email";
    public static final String BUSINESS_IMAGE = "business_image";
    public static final String BUSINESS_NUMBER = "business_number";
    public static final String BUSINESS_ADDRESS = "business_address";
    public static final String BUSINESS_DETAIL = "business_detail";
    public static final String BUSINESS_WEBSITE = "business_website";
    public static final String BUSINESS_WHATSAPP = "business_whatsapp";
    public static final String BUSINESS_INSTAGRAM = "business_instagram";
    public static final String BUSINESS_FACEBOOK = "business_facebook";
    public static final String BUSINESS_SOCIAL_MEDIA_TYPE = "business_social_media_type";

    public static final String PLAN_ID = "plan_id";
    public static final String PLAN_NAME = "plan_name";
    public static final String PLAN_START_DATE = "plan_start_date";
    public static final String PLAN_END_DATE = "plan_end_date";

    public static final String LANGUAGE_NAME = "language_name";

    public static final String PRIVACY_POLICY = "privacy_policy";
    public static final String TERM_CONDITION = "term_condition";
    public static final String REFUND_POLICY = "refund_policy";
    public static final String PRIVACY_POLICY_LINK = "privacy_policy_link";

    public static final String ONESIGNAL_APP_ID = "onesignal_app_id";
    public static final String RAZORPAY_KEY_ID = "razorpay_key_id";
    public static final String RAZORPAY_SECRET_KEY = "razorpay_secret_key";
    public static final String CONTACT_WHATSAPP = "contact_whatsapp";
    public static final String CURRENCY = "currency";

    public static final String PUBLISHER_ID = "publisher_id";
    public static final String ADS_ENABLE = "ads_enable";
    public static final String BANNER_AD_ENABLED = "banner_ad_enabled";
    public static final String BANNER_AD_ID = "banner_ad_id";
    public static final String INTERSTITIAL_AD_ENABLED = "interstitial_ad_enabled";
    public static final String INTERSTITIAL_AD_ID = "interstitial_ad_id";
    public static final String INTERSTITIAL_AD_CLICK = "interstitial_ad_click";
    public static final String NATIVE_AD_ENABLED = "native_ad_enabled";
    public static final String NATIVE_AD_ID = "native_ad_id";
    public static final String NATIVE_AD_COUNT = "native_ad_count";
    public static final String OPEN_APP_AD_ENABLED = "open_app_ad_enabled";
    public static final String OPEN_AD_ID = "open_ad_id";
    public static final String REWARD_AD_ENABLED = "reward_ad_enabled";
    public static final String REWARD_AD = "reward_ad";

    public static final String INTENT_FEST_ID = "fest_id";
    public static final String INTENT_FEST_NAME = "fest_name";
    public static final String INTENT_TYPE = "intent_type";
    public static final String INTENT_POST_IMAGE = "intent_post_image";
    public static final String INTENT_VIDEO = "intent_video";
    public static final String INTENT_IS_FROM_NOTIFICATION = "is_from_notification";

    public static final String PRF_TYPE = "prf_type";
    public static final String PRF_ID = "prf_id";
    public static final String PRF_NAME = "prf_name";

    public static HomeViewModel getHomeViewModel(AppCompatActivity activity) {
        return new ViewModelProvider(activity).get(HomeViewModel.class);
    }

    public static HomeViewModel getHomeViewModel(Fragment fragment) {
        return new ViewModelProvider(fragment).get(HomeViewModel.class);
    }

    public static UserViewModel getUserViewModel(AppCompatActivity activity) {
        return new ViewModelProvider(activity).get(UserViewModel.class);
    }
}
