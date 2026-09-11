package com.growwthapps.dailypost.v2.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/* loaded from: classes3.dex */
public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap[]> {
    private BitmapDownloadListener listener;

    /* loaded from: classes3.dex */
    public interface BitmapDownloadListener {
        void onBitmapsDownloaded(Bitmap[] bitmapArr);
    }

    public BitmapDownloaderTask(BitmapDownloadListener bitmapDownloadListener) {
        Log.d("onMessageReceived__", "BitmapDownloaderTask");
        this.listener = bitmapDownloadListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    public Bitmap[] doInBackground(String... strArr) {
        Bitmap[] bitmapArr = new Bitmap[strArr.length];

        Log.d("onMessageReceived__", "doInBackground " + bitmapArr.length);

        for (int i = 0; i < strArr.length; i++) {
            String imagePath = strArr[i];

            try {
                if (imagePath.startsWith("https://") || imagePath.startsWith("http://")) {
                    // Handle network image
                    URL url = new URL(imagePath);
                    URLConnection connection = url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    bitmapArr[i] = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } else {
                    // Handle local file path
                    Uri imageUri = Uri.parse(imagePath);
                    bitmapArr[i] = BitmapFactory.decodeFile(imageUri.getPath());
                }
            } catch (IOException e) {
                Log.e("onMessageReceived__", "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e("onMessageReceived__", "Exception: " + e.getMessage());
            }
        }
        return bitmapArr;
    }


    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Bitmap[] bitmapArr) {
        BitmapDownloadListener bitmapDownloadListener = this.listener;
        if (bitmapDownloadListener != null) {
            bitmapDownloadListener.onBitmapsDownloaded(bitmapArr);
        }
    }
}
