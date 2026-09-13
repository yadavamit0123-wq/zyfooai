package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pt.zyfooai.R;
import com.pt.zyfooai.utils.AnalyticsHelper;
import com.pt.zyfooai.utils.BiometricHelper;
import com.pt.zyfooai.utils.MediaStoreHelper;
import com.pt.zyfooai.ui.adapters.SavedAdapter;
import com.pt.zyfooai.databinding.ActivityDownloadBinding;
import com.pt.zyfooai.model.DownloadItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DownloadActivity extends AppCompatActivity {
    private ActivityDownloadBinding binding;
    private List<DownloadItem> uriList;
    private SavedAdapter adapter;
    Activity context;
    private boolean biometricVerified;
    private boolean loadTaskRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        topIconBar(this);

        uriList = new ArrayList<>();
        adapter = new SavedAdapter(context, uriList, (item) -> goToNextActivity(item.getUri()));
        binding.rvDownloads.setAdapter(adapter);
        AnalyticsHelper.logScreen(context, "downloads");

        binding.backImg.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BiometricHelper.isLockEnabled(this) && !biometricVerified) {
            BiometricHelper.authenticate(this, () -> {
                biometricVerified = true;
                loadData();
            }, this::finish);
            return;
        }
        loadData();
    }

    private void loadData() {
        if (loadTaskRunning) {
            return;
        }
        loadTaskRunning = true;
        new LoadImagesTask().execute();
    }

    private void goToNextActivity(Uri uri) {
        Intent intent = new Intent(context, ShareImageActivity.class);
        intent.putExtra("uri", uri.toString());
        startActivity(intent);
    }

    private class LoadImagesTask extends AsyncTask<Void, Void, List<DownloadItem>> {

        @Override
        protected List<DownloadItem> doInBackground(Void... voids) {
            List<DownloadItem> items = new ArrayList<>();

            File directory = MediaStoreHelper.getLegacyDownloadsDir(context);

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    Arrays.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File f1, File f2) {
                            return Long.compare(f2.lastModified(), f1.lastModified());
                        }
                    });

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

                    for (File file : files) {
                        if (isSupportedMedia(file)) {
                            boolean isVideo = isVideoFile(file);
                            String dateLabel = dateFormat.format(new Date(file.lastModified()));
                            String sizeLabel = formatFileSize(file.length());
                            items.add(new DownloadItem(Uri.fromFile(file), isVideo, dateLabel, sizeLabel));
                        }
                    }
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<DownloadItem> items) {
            super.onPostExecute(items);
            loadTaskRunning = false;

            if (items.size() > 0) {
                binding.llNotFound.setVisibility(View.GONE);
            } else {
                binding.llNotFound.setVisibility(View.VISIBLE);
            }

            uriList.clear();
            uriList.addAll(items);
            adapter.notifyDataSetChanged();
        }

        private boolean isSupportedMedia(File file) {
            String name = file.getName().toLowerCase(Locale.getDefault());
            return name.endsWith(".png")
                    || name.endsWith(".jpg")
                    || name.endsWith(".jpeg")
                    || name.endsWith(".mp4");
        }

        private boolean isVideoFile(File file) {
            return file.getName().toLowerCase(Locale.getDefault()).endsWith(".mp4");
        }

        private String formatFileSize(long bytes) {
            if (bytes < 1024) {
                return bytes + " B";
            }
            if (bytes < 1024 * 1024) {
                return String.format(Locale.getDefault(), "%.1f KB", bytes / 1024f);
            }
            return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024f * 1024f));
        }
    }
}
