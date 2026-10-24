package com.pt.zyfooai.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.pt.zyfooai.respository.ReferralRepository;

/**
 * After successful loginUser → user_id, call POST apply-reffer once if user entered a code.
 */
public final class ReferralApplyHelper {

    private ReferralApplyHelper() {
    }

    public static void savePendingCode(Context context, @Nullable String code) {
        PreferenceManager prefs = new PreferenceManager(context);
        if (code == null || code.trim().isEmpty()) {
            prefs.setString(Constant.PENDING_REFERRAL_CODE, "");
            return;
        }
        prefs.setString(Constant.PENDING_REFERRAL_CODE, code.trim());
    }

    public static void applyPendingIfAny(Context context, LifecycleOwner owner, String userId,
                                         @Nullable Runnable onDone) {
        PreferenceManager prefs = new PreferenceManager(context);
        String code = prefs.getString(Constant.PENDING_REFERRAL_CODE);
        if (code == null || code.isEmpty() || userId == null || userId.isEmpty()) {
            if (onDone != null) {
                onDone.run();
            }
            return;
        }

        String deviceId = MyUtils.getDeviceId(context);
        new ReferralRepository().apply(userId, code, deviceId).observe(owner, response -> {
            prefs.setString(Constant.PENDING_REFERRAL_CODE, "");
            if (response != null) {
                if (response.walletBalance != null) {
                    prefs.setString(Constant.REFER_WALLET_BALANCE, response.walletBalance);
                }
                String msg = response.message;
                if (msg != null && !msg.isEmpty()) {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                } else if (response.isSuccess()) {
                    Toast.makeText(context, "Referral applied", Toast.LENGTH_SHORT).show();
                }
            }
            if (onDone != null) {
                onDone.run();
            }
        });
    }
}
