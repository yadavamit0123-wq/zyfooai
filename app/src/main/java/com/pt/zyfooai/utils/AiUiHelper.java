package com.pt.zyfooai.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.pt.zyfooai.R;
import com.pt.zyfooai.ui.activities.SubscriptionActivity;

public final class AiUiHelper {

    private AiUiHelper() {
    }

    public static boolean handleQuotaError(Activity activity, String code, String message) {
        if (activity == null) {
            return false;
        }
        String c = code != null ? code.trim().toUpperCase() : "";
        String msg = message;
        if ("PACKAGE_REQUIRED".equals(c) || c.contains("PACKAGE")) {
            if (TextUtils.isEmpty(msg)) {
                msg = activity.getString(R.string.ai_package_required);
            }
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            activity.startActivity(new Intent(activity, SubscriptionActivity.class));
            return true;
        }
        if ("DAILY_LIMIT".equals(c) || c.contains("LIMIT") || c.contains("QUOTA")) {
            if (TextUtils.isEmpty(msg)) {
                msg = activity.getString(R.string.ai_daily_limit);
            }
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
