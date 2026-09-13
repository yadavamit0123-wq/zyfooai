package com.pt.zyfooai.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pt.zyfooai.model.PostItem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class OfflinePostsCache {

    private static final String CACHE_FILE = AppConstants.OFFLINE_CACHE_FILE;
    private static final Gson GSON = new Gson();

    private OfflinePostsCache() {
    }

    public static void save(Context context, List<PostItem> posts) {
        if (context == null || posts == null || posts.isEmpty()) {
            return;
        }
        try {
            File file = new File(context.getCacheDir(), CACHE_FILE);
            FileWriter writer = new FileWriter(file, false);
            writer.write(GSON.toJson(posts));
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static List<PostItem> load(Context context) {
        if (context == null) {
            return new ArrayList<>();
        }
        try {
            File file = new File(context.getCacheDir(), CACHE_FILE);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            Type type = new TypeToken<List<PostItem>>() {
            }.getType();
            FileReader reader = new FileReader(file);
            List<PostItem> posts = GSON.fromJson(reader, type);
            reader.close();
            return posts != null ? posts : new ArrayList<>();
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }
}
