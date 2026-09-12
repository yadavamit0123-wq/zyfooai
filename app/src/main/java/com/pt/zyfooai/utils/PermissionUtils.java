package com.pt.zyfooai.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    Activity activity;
    ActivityResultLauncher<String[]> mPermissionResult;

    public PermissionUtils(Activity activity, ActivityResultLauncher<String[]> mPermissionResult) {
        this.activity = activity;
        this.mPermissionResult=mPermissionResult;
    }

    public void requestPermissions()
    {
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT > 31) {
            permissions = new String[]{ Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS,  Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        }

        mPermissionResult.launch(permissions);
    }

    public boolean isPermissiongGranted()
    {
        if (android.os.Build.VERSION.SDK_INT > 31) {
            int readPhotoStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES);
            int readAUDIOStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO);
            int readVideoStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO);
            int postStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS);
            int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            return (readPhotoStoragePermission== PackageManager.PERMISSION_GRANTED && readAUDIOStoragePermission== PackageManager.PERMISSION_GRANTED && readVideoStoragePermission== PackageManager.PERMISSION_GRANTED && postStoragePermission== PackageManager.PERMISSION_GRANTED && cameraPermission== PackageManager.PERMISSION_GRANTED);
        } else {
            int readExternalStoragePermission= ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalStoragePermission= ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission= ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            return (readExternalStoragePermission== PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission== PackageManager.PERMISSION_GRANTED && cameraPermission == PackageManager.PERMISSION_GRANTED);
        }
    }

}
