package com.growwthapps.dailypost.v2.utils;

import static android.os.Build.VERSION.SDK_INT;
import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;


import static com.growwthapps.dailypost.v2.utils.PathUtil.isMediaDocument;
import static com.yalantis.ucrop.util.FileUtils.getDataColumn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.model.AdsModel;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MyUtils {

    public static AdsModel model;
    public static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    public static PreferenceManager preferenceManager;
    private static String TAG = "AdmobAppId";
    public Activity context;

    public static void showResponse(Object onject) {


        Log.i("MyResponse " + onject.getClass().getSimpleName(), "" + new Gson().toJson(onject));

    }

    public static String getDeviceId(Context context) {
        return android.provider.Settings.Secure.getString(
                context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static void topIconBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getColor(R.color.white));
            activity.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);
        }
    }

    public static void hideNavigation(Activity context, boolean statusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (statusBar) {

                window.setStatusBarColor(context.getColor(R.color.bg_screen));
                context.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);

            } else {
                window.setStatusBarColor(context.getColor(R.color.transparent));
                context.getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS);
            }

        }


    }

    public static String getAppFolder(Context activity) {
        return activity.getExternalFilesDir(null).getPath() + "/";
    }

    public static class GetImageFileAsync extends AsyncTask<String, Void, File> {

        Activity context;
        Bitmap bitmap;

        OnBitmapSaved mBitmapSavedListener;
        ProgressDialog dialog;

        public GetImageFileAsync(Activity context, Bitmap bitmap) {
            this.context = context;
            this.bitmap = bitmap;


            dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait...");

            dialog.setCancelable(false);
            dialog.show();


            this.execute();
        }

        public void onBitmapSaved(OnBitmapSaved mlistener) {
            this.mBitmapSavedListener = mlistener;
        }

        @Override
        protected File doInBackground(String... strings) {


            String path = context.getCacheDir().getAbsolutePath();

            String name = System.currentTimeMillis() + ".png";

            File file = null;

            try {

                OutputStream fOut = null;
                Integer counter = 0;


                file = new File(path, name);

                if (!file.exists())
                    file.createNewFile();
                // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                fOut = new FileOutputStream(file);

                Bitmap pictureBitmap = bitmap; // obtaining the Bitmap
                pictureBitmap.compress(Bitmap.CompressFormat.PNG, 70, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

            } catch (Exception e) {
                e.printStackTrace();
            }

            return file;
        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);

            dialog.dismiss();

            mBitmapSavedListener.onSaved(s);
        }

        public interface OnBitmapSaved {
            void onSaved(File file);
        }
    }

}
