package com.pt.zyfooai.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.pt.zyfooai.BuildConfig;

public final class AppUpdateHelper {

    private static final String TAG = "AppUpdateHelper";
    public static final int UPDATE_REQUEST_CODE = 9911;

    private AppUpdateHelper() {
    }

    public static void checkForUpdate(Activity activity) {
        PreferenceManager prefs = new PreferenceManager(activity);
        int forceVersion = prefs.getInt(Constant.FORCE_UPDATE_VERSION);
        if (forceVersion > BuildConfig.VERSION_CODE) {
            showForceUpdateDialog(activity, prefs.getString(Constant.UPDATE_MESSAGE));
            return;
        }

        AppUpdateManager manager = AppUpdateManagerFactory.create(activity);
        manager.getAppUpdateInfo().addOnSuccessListener(info -> {
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    manager.startUpdateFlowForResult(
                            info,
                            AppUpdateType.FLEXIBLE,
                            activity,
                            UPDATE_REQUEST_CODE
                    );
                } catch (Exception e) {
                    Log.w(TAG, "In-app update flow failed", e);
                }
            }
        });
    }

    private static void showForceUpdateDialog(Activity activity, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle("Update Required")
                .setMessage(message != null && !message.isEmpty() ? message : "Please update the app to continue.")
                .setCancelable(false)
                .setPositiveButton("Update", (d, w) -> openPlayStore(activity))
                .show();
    }

    public static void openPlayStore(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (Exception e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            Toast.makeText(activity, "Opening Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    public static void handleActivityResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == UPDATE_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "User declined in-app update");
        }
    }
}
