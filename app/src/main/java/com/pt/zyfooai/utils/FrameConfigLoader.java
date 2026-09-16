package com.pt.zyfooai.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.pt.zyfooai.model.FrameConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Loads {@link FrameConfig} JSON bundled in assets or from raw strings.
 */
public final class FrameConfigLoader {

    private static final Gson GSON = new Gson();

    private FrameConfigLoader() {
    }

    public static FrameConfig fromAsset(Context context, String assetPath) {
        if (context == null || assetPath == null || assetPath.isEmpty()) {
            return null;
        }
        try (InputStream inputStream = context.getAssets().open(assetPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return fromJson(builder.toString());
        } catch (IOException ignored) {
            return null;
        }
    }

    public static FrameConfig fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return GSON.fromJson(json, FrameConfig.class);
        } catch (Exception ignored) {
            return null;
        }
    }
}
