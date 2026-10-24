package com.pt.zyfooai.utils;

import android.content.Context;

import com.pt.zyfooai.model.AppInfo;
import com.pt.zyfooai.model.AppInfos;
import com.pt.zyfooai.model.ReferEarnConfig;
import com.pt.zyfooai.model.ReferralMeResponse;

/**
 * Persist Refer & Earn config from getAppInfo / referral/me. Amounts never hardcoded in UI.
 */
public final class ReferEarnPrefs {

    private ReferEarnPrefs() {
    }

    public static void saveFromAppInfo(Context context, AppInfos appInfos) {
        if (context == null || appInfos == null) {
            return;
        }
        PreferenceManager prefs = new PreferenceManager(context);
        ReferEarnConfig cfg = appInfos.getReferEarn();
        AppInfo appInfo = appInfos.getAppInfo();

        boolean enabled = false;
        String signupBonus = "";
        String referrerAmount = "";
        String prefix = "";
        String suffix = "";
        String inviteUrl = "";

        if (cfg != null) {
            enabled = cfg.isEnabled();
            signupBonus = nullToEmpty(cfg.signupBonus);
            referrerAmount = nullToEmpty(cfg.resolveReferrerAmount());
            prefix = nullToEmpty(cfg.inviteMessagePrefix);
            suffix = nullToEmpty(cfg.inviteMessageSuffix);
            inviteUrl = nullToEmpty(cfg.inviteUrl);
            saveBanner(prefs, cfg.bannerType, cfg.bannerUrl, cfg.bannerPosterUrl);
        }

        if (appInfo != null) {
            if (appInfo.referEarnEnabled != null) {
                enabled = isTruthy(appInfo.referEarnEnabled) || enabled;
            }
            if (signupBonus.isEmpty() && appInfo.signupBonus != null) {
                signupBonus = appInfo.signupBonus;
            }
            if (prefix.isEmpty() && appInfo.inviteMessagePrefix != null) {
                prefix = appInfo.inviteMessagePrefix;
            }
            if (suffix.isEmpty() && appInfo.inviteMessageSuffix != null) {
                suffix = appInfo.inviteMessageSuffix;
            }
            if (inviteUrl.isEmpty() && appInfo.inviteUrl != null) {
                inviteUrl = appInfo.inviteUrl;
            }
        }

        prefs.setBoolean(Constant.REFER_EARN_ENABLED, enabled);
        prefs.setString(Constant.REFER_SIGNUP_BONUS, signupBonus);
        prefs.setString(Constant.REFER_REFERRER_AMOUNT, referrerAmount);
        prefs.setString(Constant.REFER_INVITE_PREFIX, prefix);
        prefs.setString(Constant.REFER_INVITE_SUFFIX, suffix);
        prefs.setString(Constant.REFER_INVITE_URL, inviteUrl);
    }

    public static void saveFromMe(Context context, ReferralMeResponse me) {
        if (context == null || me == null) {
            return;
        }
        PreferenceManager prefs = new PreferenceManager(context);
        if (me.referralCode != null && !me.referralCode.isEmpty()) {
            prefs.setString(Constant.REFER_SERVER_CODE, me.referralCode);
        }
        if (me.signupBonus != null && !me.signupBonus.isEmpty()) {
            prefs.setString(Constant.REFER_SIGNUP_BONUS, me.signupBonus);
        }
        String amount = me.resolveReferrerAmount();
        if (!amount.isEmpty()) {
            prefs.setString(Constant.REFER_REFERRER_AMOUNT, amount);
        }
        if (me.inviteMessagePrefix != null) {
            prefs.setString(Constant.REFER_INVITE_PREFIX, me.inviteMessagePrefix);
        }
        if (me.inviteMessageSuffix != null) {
            prefs.setString(Constant.REFER_INVITE_SUFFIX, me.inviteMessageSuffix);
        }
        if (me.inviteUrl != null && !me.inviteUrl.isEmpty()) {
            prefs.setString(Constant.REFER_INVITE_URL, me.inviteUrl);
        }
        if (me.walletBalance != null) {
            prefs.setString(Constant.REFER_WALLET_BALANCE, me.walletBalance);
        }
        saveBanner(prefs, me.bannerType, me.bannerUrl, me.bannerPosterUrl);
    }

    private static void saveBanner(PreferenceManager prefs, String type, String url, String poster) {
        if (type != null) {
            prefs.setString(Constant.REFER_BANNER_TYPE, type.trim().toLowerCase());
        }
        if (url != null) {
            prefs.setString(Constant.REFER_BANNER_URL, url.trim());
        }
        if (poster != null) {
            prefs.setString(Constant.REFER_BANNER_POSTER_URL, poster.trim());
        }
    }

    public static boolean isEnabled(Context context) {
        return new PreferenceManager(context).getBoolean(Constant.REFER_EARN_ENABLED);
    }

    public static String buildShareMessage(Context context, String serverCode) {
        PreferenceManager prefs = new PreferenceManager(context);
        String code = serverCode != null && !serverCode.isEmpty()
                ? serverCode
                : prefs.getString(Constant.REFER_SERVER_CODE);
        String prefix = prefs.getString(Constant.REFER_INVITE_PREFIX);
        String suffix = prefs.getString(Constant.REFER_INVITE_SUFFIX);
        String url = prefs.getString(Constant.REFER_INVITE_URL);
        if (url == null || url.isEmpty()) {
            url = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        }
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        if (code == null) {
            code = "";
        }
        return prefix + code + suffix + "\n" + url;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static boolean isTruthy(Object raw) {
        if (raw instanceof Boolean) {
            return (Boolean) raw;
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue() == 1;
        }
        String s = String.valueOf(raw).trim();
        return "1".equals(s) || "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
    }
}
