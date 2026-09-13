package com.pt.zyfooai.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class MediaStoreHelper {

    private MediaStoreHelper() {
    }

    public static String getDownloadsFolderPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Environment.DIRECTORY_PICTURES + File.separator + getAppFolderName(context);
        }
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getAppFolderName(context));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    public static File getLegacyDownloadsDir(Context context) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getAppFolderName(context));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static Uri saveImage(Context context, byte[] data, String fileName) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + getAppFolderName(context));
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                throw new IOException("Unable to create image in MediaStore");
            }
            try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
                if (out != null) {
                    out.write(data);
                }
            }
            return uri;
        }
        File outFile = new File(getLegacyDownloadsDir(context), fileName);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile)) {
            fos.write(data);
        }
        return Uri.fromFile(outFile);
    }

    public static Uri saveVideo(Context context, File sourceFile, String fileName) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + getAppFolderName(context));
            Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                throw new IOException("Unable to create video in MediaStore");
            }
            try (OutputStream out = context.getContentResolver().openOutputStream(uri);
                 FileInputStream in = new FileInputStream(sourceFile)) {
                if (out == null) {
                    throw new IOException("Unable to open output stream");
                }
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            return uri;
        }
        File dest = new File(getLegacyDownloadsDir(context), fileName);
        copyFile(sourceFile, dest);
        return Uri.fromFile(dest);
    }

    private static void copyFile(File source, File dest) throws IOException {
        try (FileInputStream in = new FileInputStream(source);
             java.io.FileOutputStream out = new java.io.FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }

    private static String getAppFolderName(Context context) {
        return context.getString(com.pt.zyfooai.R.string.app_name);
    }
}
