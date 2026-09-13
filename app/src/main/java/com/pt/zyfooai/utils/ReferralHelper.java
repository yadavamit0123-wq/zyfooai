package com.pt.zyfooai.utils;

import android.content.Context;
import android.content.Intent;

public final class ReferralHelper {

    public static final String REFERRAL_CODE = "referral_code";
    public static final String REFERRAL_COUNT = "referral_count";
    public static final String REFERRAL_REWARD_EARNED = "referral_reward_earned";

    private ReferralHelper() {
    }

    public static String getOrCreateReferralCode(Context context) {
        PreferenceManager prefs = new PreferenceManager(context);
        String code = prefs.getString(REFERRAL_CODE);
        if (code != null && !code.isEmpty()) {
            return code;
        }
        String userId = prefs.getString(Constant.USER_ID);
        if (userId == null || userId.isEmpty()) {
            userId = "ZY";
        }
        code = "ZY" + Math.abs(userId.hashCode() % 1000000);
        prefs.setString(REFERRAL_CODE, code);
        return code;
    }

    public static void applyReferralCode(Context context, String code) {
        if (code == null || code.isEmpty()) {
            return;
        }
        PreferenceManager prefs = new PreferenceManager(context);
        String myCode = getOrCreateReferralCode(context);
        if (code.equalsIgnoreCase(myCode)) {
            return;
        }
        int count = prefs.getInt(REFERRAL_COUNT) + 1;
        prefs.setInt(REFERRAL_COUNT, count);
        if (count >= 3) {
            prefs.setBoolean(REFERRAL_REWARD_EARNED, true);
        }
    }

    public static int getReferralCount(Context context) {
        return new PreferenceManager(context).getInt(REFERRAL_COUNT);
    }

    public static boolean hasReferralReward(Context context) {
        return new PreferenceManager(context).getBoolean(REFERRAL_REWARD_EARNED);
    }

    public static Intent buildShareIntent(Context context) {
        String code = getOrCreateReferralCode(context);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Join ZyFoo AI");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Create amazing business posts with ZyFoo AI! Use my referral code: "
                        + code + "\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName());
        return Intent.createChooser(intent, "Invite friends");
    }
}
