package com.growwthapps.dailypost.v2;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.growwthapps.dailypost.v2.ui.BitmapDownloaderTask;
import com.growwthapps.dailypost.v2.ui.activities.CustomSplashActivity;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.utils.Util;
//import com.huawei.hms.framework.common.ContextCompat;
import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class NotificationServiceExtension implements OneSignal.OSRemoteNotificationReceivedHandler {

    public static final String CHANNEL_ID = "channel";
    public String catType = "";
    public String externalLink = "";
    Bitmap posterBitmap = null;
    Bitmap profileBitmap = null;
    Bitmap newBitmap = null;
    public Context context;
    PreferenceManager preferenceManager;
    private String bigpicture = null;
    private String title;
    private String message;
    Bitmap resizedImage;

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {
        this.context = context;
        preferenceManager = new PreferenceManager(context);

        OSNotification notification = osNotificationReceivedEvent.getNotification();
        Util.showLog("NOTIFICATION:111 " + notification.toString());

        OSMutableNotification mutableNotification = notification.mutableCopy();
        mutableNotification.setExtender(builder -> {
            // Sets the accent color to Green on Android 5+ devices.
            // Accent color controls icon and action buttons on Android 5+. Accent color does not change app title on Android 10+
            builder.setColor(new BigInteger("FF00FF00", 16).intValue());
            // Sets the notification Title to Red
            Spannable spannableTitle = new SpannableString(notification.getTitle());
            spannableTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, notification.getTitle().length(), 0);
            builder.setContentTitle(spannableTitle);
            // Sets the notification Body to Blue
            Spannable spannableBody = new SpannableString(notification.getBody());
            spannableBody.setSpan(new ForegroundColorSpan(Color.BLUE), 0, notification.getBody().length(), 0);
            builder.setContentText(spannableBody);
            //Force remove push from Notification Center after 30 seconds
            builder.setTimeoutAfter(30000);
            return builder;
        });
        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        // To omit displaying a notification, pass `null` to complete()
        JSONObject data = notification.getAdditionalData();

        Log.d("remoteNotificationReceived", "remoteNotificationReceived: " + data);
        title = notification.getTitle();
        message = notification.getBody();
        bigpicture = notification.getBigPicture();

        newBitmap = getBitmapFromURL(this.bigpicture);

        if (preferenceManager.getString(Constant.USER_IMAGE) !=null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()){
            profileBitmap = getBitmapFromURL(preferenceManager.getString(Constant.USER_IMAGE));
            resizedImage = resizeImage(profileBitmap, 100, 100);

        }

        Util.showLog(data.toString());
        try {

            catType = data.getString("type");
            if (catType.contains(Constant.EXTERNAL)) {
                externalLink = data.getString("externalLink");
            }

        } catch (JSONException e) {
            Util.showErrorLog(e.getMessage(), e);
        }
        osNotificationReceivedEvent.complete(null);

        sendAutoNotification();


    }

    private void sendAutoNotification() {
        Log.d("onMessageReceived__", "sendAutoNotification");
        createNotificationChannel(context);
        Intent intent;
        if (catType.contains(Constant.EXTERNAL) && !externalLink.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(externalLink));
        }else {
            intent = new Intent(context, CustomSplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        final PendingIntent activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        new BitmapDownloaderTask(bitmapArr -> {
            Log.e("sendAutoNotification__", "Bitmap Ready");
            Bitmap bitmap = bitmapArr[0];
            if (bitmap != null && bitmapArr[1] != null) {

                NotificationServiceExtension.this.posterBitmap = bitmap;

                NotificationCompat.Builder contentIntent = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(activity)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setTicker(message);

                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setBigContentTitle(title)
                        .setSummaryText(message);

                contentIntent.setStyle(bigPictureStyle);

//                NotificationManager notificationManager = (NotificationManager) ContextCompat.getSystemService(context, "notification");
//                if (Build.VERSION.SDK_INT >= 26) {
//                    notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "QuotesPush", NotificationManager.IMPORTANCE_HIGH));
//                }
//                notificationManager.notify(new CodeGenerator().createCode("1"), contentIntent.build());
                return;
            }
            Log.e("sendAutoNotification__", "Bitmap download failed");
        }).execute(bigpicture, bigpicture);

        Log.d("sendAutoNotification__11", "remoteNotificationReceived: " + bigpicture + " 11" + preferenceManager.getString(Constant.USER_IMAGE));
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "All Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Important Notifications");
            ((NotificationManager) context.getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
        }
    }

    public class CodeGenerator {
        private HashMap<String, Integer> codeMap = new HashMap<>();

        public CodeGenerator() {
        }

        public int createCode(String str) {
            if (this.codeMap.containsKey(str)) {
                return this.codeMap.get(str).intValue();
            }
            int hashCode = str.hashCode();
            this.codeMap.put(str, Integer.valueOf(hashCode));
            return hashCode;
        }
    }

    public static Bitmap getBitmapFromURL(String str) {
        InputStream inputStream;
        try {
            URL url = new URL(str);
            if (new URL(str).openConnection() instanceof HttpsURLConnection) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.connect();
                inputStream = httpsURLConnection.getInputStream();
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
            }
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e("sendAutoNotification__", e.getMessage());
            return null;
        }
    }

    public Bitmap resizeImage(Bitmap originalImage, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(originalImage, targetWidth, targetHeight, false);
    }

}
