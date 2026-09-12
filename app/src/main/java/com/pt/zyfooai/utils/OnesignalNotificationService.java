package com.pt.zyfooai.utils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.pt.zyfooai.MyApplication;
import com.pt.zyfooai.R;
import com.pt.zyfooai.ui.BitmapDownloaderTask;
import com.pt.zyfooai.ui.activities.MainActivity;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("unused")
public class OnesignalNotificationService implements OneSignal.OSRemoteNotificationReceivedHandler {

    Context context;
    String CHANNEL_ID = "notify";
    int NOTIFICATION_ID = 1;
    String title, message, type, externalLink;
    String bigpicture = null;
    PreferenceManager preferenceManager;

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {
        this.context = context;
        preferenceManager = new PreferenceManager(context);
        Log.d("onMessageReceived__", ""+osNotificationReceivedEvent.getNotification().getAdditionalData());
        checkNotification(osNotificationReceivedEvent);
    }

    private void checkNotification(OSNotificationReceivedEvent osNotificationReceivedEvent) {
        OSNotification notification = osNotificationReceivedEvent.getNotification();
        if (notification.getAdditionalData() != null) {
            JSONObject object = notification.getAdditionalData();
            try {
                title = notification.getTitle();
                message = notification.getBody();
                type = object.getString("type");

                if (object.has("big_picture")) {
                    bigpicture = object.getString("big_picture");
                }

            } catch (Exception e) {
                Log.e("onMessageReceived__", "Exception: " + e.getMessage());
            }

//            Log.d("onMessageReceived__", "type: " + type);
//            if (type != null && type.equals("AUTO_NOTIFY")) {
                sendAutoNotification();
//            } else {
//                Log.d("onMessageReceived__", "OS Notify");
//                osNotificationReceivedEvent.complete(notification);
//            }
        }
    }

    Bitmap posterBitmap = null;
    Bitmap profileBitmap = null;
    private void sendAutoNotification() {
        Log.d("onMessageReceived__", "Auto Notify");

        createNotificationChannel(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int backgroundColor = getRandomDarkColor();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String profileUrl = preferenceManager.getString("DataType").equals("Business") ?
                preferenceManager.getString(Constant.BUSINESS_IMAGE) :
                preferenceManager.getString(Constant.USER_IMAGE);

        Log.d("onMessageReceived__", "profileUrl: " + profileUrl);
        String[] urls = {bigpicture, profileUrl};
        new BitmapDownloaderTask(bitmaps -> {
            if (bitmaps[0] != null && bitmaps[1] != null) {

                Log.d("onMessageReceived__", "Auto Notify 2");

                posterBitmap = bitmaps[0];
                profileBitmap = getCircularBitmap(bitmaps[1]);

                RemoteViews expandedView;
                RemoteViews collapsedView;
                try {
                    expandedView = new RemoteViews(MyApplication.getInstance().getPackageName(), R.layout.custome_notification_expend);
                    collapsedView = new RemoteViews(MyApplication.getInstance().getPackageName(), R.layout.custome_notification_collapse);
                } catch (IllegalArgumentException e) {
                    Log.e("onMessageReceived__", "Error creating RemoteViews", e);
                    return;
                }

                expandedView.setTextViewText(R.id.user_name, preferenceManager.getString("DataType").equals("Business") ? preferenceManager.getString(Constant.BUSINESS_NAME) : preferenceManager.getString(Constant.USER_NAME));
                expandedView.setImageViewBitmap(R.id.iv, posterBitmap);
                expandedView.setImageViewBitmap(R.id.profilePic, profileBitmap);
                expandedView.setImageViewBitmap(R.id.profilePic2, profileBitmap);
                expandedView.setTextViewText(R.id.description,  preferenceManager.getString("DataType").equals("Business") ? preferenceManager.getString(Constant.BUSINESS_NAME) : preferenceManager.getString(Constant.USER_NAME)+ " जी, " + title + " " + message);
                expandedView.setInt(R.id.mainLay, "setBackgroundColor", backgroundColor);

                collapsedView.setImageViewBitmap(R.id.iv, posterBitmap);
                collapsedView.setImageViewBitmap(R.id.profilePic, profileBitmap);
                collapsedView.setTextViewText(R.id.description, preferenceManager.getString("DataType").equals("Business") ? preferenceManager.getString(Constant.BUSINESS_NAME) : preferenceManager.getString(Constant.USER_NAME) + " जी, " + title + " " + message);
                collapsedView.setInt(R.id.mainLay, "setBackgroundColor", backgroundColor);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setCustomContentView(collapsedView)
                        .setSmallIcon(R.drawable.logo)
                        .setCustomBigContentView(expandedView)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(contentIntent);

                mBuilder.setContentTitle(title);
                mBuilder.setContentText(message);
                mBuilder.setTicker(message);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel mChannel;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "QuotesPush";// The user-visible name of the channel.
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mNotificationManager.createNotificationChannel(mChannel);
                }

                CodeGenerator generator = new CodeGenerator();
                int code = generator.createCode(title);
                mNotificationManager.notify(code, mBuilder.build());

                Log.d("onMessageReceived__", "Auto Notified");
            } else {
                Log.e("onMessageReceived__", "Bitmap download failed");
            }
        }).execute(urls);

    }

    public class CodeGenerator {

        private HashMap<String, Integer> codeMap;
        private int RANDOM_CODE_BOUND = 1000000;

        public CodeGenerator() {
            codeMap = new HashMap<>();
        }

        public int createCode(String title) {
            if (title == null || title.isEmpty()) {
                return generateRandomCode();
            }else {
                if (codeMap.containsKey(title)) {
                    return codeMap.get(title);
                } else {
                    // Generate a new code using the hash code of the string
                    int code = title.hashCode();
                    codeMap.put(title, code);
                    return code;
                }
            }
        }

        private int generateRandomCode() {
            Random random = new Random();
            return random.nextInt(RANDOM_CODE_BOUND);
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, diameter, diameter);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, (width - diameter) / 2, (height - diameter) / 2, paint);
        return output;
    }

    private int getRandomDarkColor() {
        int maxColorValue = 128; // Adjust this value to control the darkness of the generated colors
        int r = (int) (Math.random() * maxColorValue);
        int g = (int) (Math.random() * maxColorValue);
        int b = (int) (Math.random() * maxColorValue);
        return Color.rgb(r, g, b);
    }

//    public void createNotificationChannel(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "All Notifications";
//            String description = "Important Notifications";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                CharSequence name = "All Notifications";
                String description = "Important Notifications";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
