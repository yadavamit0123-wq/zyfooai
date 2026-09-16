package com.pt.zyfooai.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pt.zyfooai.model.FrameConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Disk cache for server-published PNG overlay frames.
 */
public final class FrameCache {

    public static final String PREF_FRAMES_CACHE_VERSION = "frames_cache_version";
    public static final String PREF_FRAMES_CACHE_UPDATED_AT = "frames_cache_updated_at";

    private static final Gson GSON = new Gson();
    private static final String FILE_IMAGE = "frames_cache_image.json";
    private static final String FILE_REELS = "frames_cache_reels.json";

    private FrameCache() {
    }

    public static void save(Context context, FrameMediaType mediaType, List<FrameConfig> frames) {
        if (context == null) {
            return;
        }
        List<FrameConfig> safeList = frames != null ? frames : new ArrayList<>();
        try {
            File file = cacheFile(context, mediaType);
            FileWriter writer = new FileWriter(file, false);
            writer.write(GSON.toJson(safeList));
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static List<FrameConfig> load(Context context, FrameMediaType mediaType) {
        if (context == null) {
            return new ArrayList<>();
        }
        try {
            File file = cacheFile(context, mediaType);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            Type type = new TypeToken<List<FrameConfig>>() {
            }.getType();
            FileReader reader = new FileReader(file);
            List<FrameConfig> frames = GSON.fromJson(reader, type);
            reader.close();
            return frames != null ? frames : new ArrayList<>();
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    public static void saveMeta(Context context, int version, String updatedAt) {
        if (context == null) {
            return;
        }
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setInt(PREF_FRAMES_CACHE_VERSION, version);
        if (updatedAt != null && !updatedAt.isEmpty()) {
            preferenceManager.setString(PREF_FRAMES_CACHE_UPDATED_AT, updatedAt);
        }
    }

    public static int cachedVersion(Context context) {
        if (context == null) {
            return 0;
        }
        return new PreferenceManager(context).getInt(PREF_FRAMES_CACHE_VERSION, 0);
    }

    public static List<FrameConfig> publishedFrames(List<FrameConfig> frames) {
        if (frames == null || frames.isEmpty()) {
            return Collections.emptyList();
        }
        List<FrameConfig> published = new ArrayList<>();
        for (FrameConfig frame : frames) {
            if (frame == null || !frame.hasRenderableOverlay()) {
                continue;
            }
            if (frame.status != null
                    && !frame.status.isEmpty()
                    && !"published".equalsIgnoreCase(frame.status)) {
                continue;
            }
            published.add(frame);
        }
        published.sort((left, right) -> Integer.compare(left.sortOrder, right.sortOrder));
        return published;
    }

    private static File cacheFile(Context context, FrameMediaType mediaType) {
        String fileName = mediaType == FrameMediaType.REELS ? FILE_REELS : FILE_IMAGE;
        return new File(context.getCacheDir(), fileName);
    }
}
