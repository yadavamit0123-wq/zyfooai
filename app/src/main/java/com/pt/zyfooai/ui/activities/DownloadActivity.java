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
import com.pt.zyfooai.ui.adapters.SavedAdapter;
import com.pt.zyfooai.databinding.ActivityDownloadBinding;
import com.pt.zyfooai.model.DownloadItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {
    private ActivityDownloadBinding binding;
    private List<DownloadItem> uriList;
    private SavedAdapter adapter;
    Activity context;

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

        loadData();


        binding.backImg.setOnClickListener(v -> {onBackPressed();});


    }

    private void loadData() {
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

            String folderPath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name);

            File directory = new File(folderPath);

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    // Sort files by last modified date in descending order
                    Arrays.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File f1, File f2) {
                            return Long.compare(f2.lastModified(), f1.lastModified());
                        }
                    });

                    for (File file : files) {
                        if (isImageFile(file)) {
                            items.add(new DownloadItem(Uri.fromFile(file), false));
                        }
                    }
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<DownloadItem> items) {
            super.onPostExecute(items);

            if (items.size() > 0) {
                binding.llNotFound.setVisibility(View.GONE);
            } else {
                binding.llNotFound.setVisibility(View.VISIBLE);
            }

            uriList.clear();
            uriList.addAll(items);
            adapter.notifyDataSetChanged();
        }

        private boolean isImageFile(File file) {
            String name = file.getName();
            String extension = name.substring(name.lastIndexOf(".") + 1);
            return extension.equalsIgnoreCase("png") ||
                    extension.equalsIgnoreCase("jpg") ||
                    extension.equalsIgnoreCase("jpeg");
        }
    }
}