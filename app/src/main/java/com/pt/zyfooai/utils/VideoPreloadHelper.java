package com.pt.zyfooai.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.pt.zyfooai.model.PostItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Warms upcoming feed media for the next ~3–4 scrolls:
 * videos → ExoPlayer {@link VideoCacheHelper}; images → Glide disk cache.
 */
public final class VideoPreloadHelper {

    private static final String TAG = "VideoPreloadHelper";
    /** ~1.5MB first chunk — enough for quick start on most progressive MP4s. */
    private static final long PRELOAD_BYTES = 1_500_000L;
    /** Next 3–4 scrolls of reels. */
    public static final int MAX_VIDEO_PRELOAD = 4;
    /** Next 3–4 scrolls of images. */
    public static final int MAX_IMAGE_PRELOAD = 4;

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Set<String> IN_FLIGHT = new HashSet<>();
    private static final Set<String> DONE = new HashSet<>();
    private static final Set<String> IMAGE_DONE = new HashSet<>();

    private VideoPreloadHelper() {
    }

    /** Preload first upcoming videos + images in the feed list. */
    public static void preloadUpcoming(Context context, @Nullable List<PostItem> feed) {
        preloadNextAfter(context, feed, 0);
    }

    /** Preload the next media after {@code fromIndex} (inclusive search forward). */
    public static void preloadNextAfter(Context context, @Nullable List<PostItem> feed, int fromIndex) {
        if (context == null || feed == null || feed.isEmpty()) {
            return;
        }
        Context app = context.getApplicationContext();
        int start = Math.max(0, fromIndex);
        int videos = 0;
        int images = 0;
        for (int i = start; i < feed.size(); i++) {
            if (videos >= MAX_VIDEO_PRELOAD && images >= MAX_IMAGE_PRELOAD) {
                break;
            }
            PostItem post = feed.get(i);
            if (post == null) {
                continue;
            }
            if (post.is_video) {
                if (videos >= MAX_VIDEO_PRELOAD) {
                    continue;
                }
                String url = post.image_url;
                if (url != null && !url.trim().isEmpty() && enqueueVideo(app, url.trim())) {
                    videos++;
                }
            } else {
                if (images >= MAX_IMAGE_PRELOAD) {
                    continue;
                }
                String url = post.image_url;
                if (url != null && !url.trim().isEmpty() && enqueueImage(app, url.trim())) {
                    images++;
                }
            }
        }
    }

    private static boolean enqueueVideo(Context appContext, String url) {
        synchronized (IN_FLIGHT) {
            if (DONE.contains(url) || IN_FLIGHT.contains(url)) {
                return false;
            }
            IN_FLIGHT.add(url);
        }
        EXECUTOR.execute(() -> {
            try {
                warmCache(appContext, url);
                synchronized (IN_FLIGHT) {
                    DONE.add(url);
                }
                Log.d(TAG, "Video preloaded: " + url);
            } catch (Exception e) {
                Log.d(TAG, "Video preload failed: " + e.getMessage());
            } finally {
                synchronized (IN_FLIGHT) {
                    IN_FLIGHT.remove(url);
                }
            }
        });
        return true;
    }

    private static boolean enqueueImage(Context appContext, String url) {
        synchronized (IMAGE_DONE) {
            if (IMAGE_DONE.contains(url)) {
                return false;
            }
            IMAGE_DONE.add(url);
        }
        try {
            Glide.with(appContext).load(url).preload();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Image preload failed: " + e.getMessage());
            synchronized (IMAGE_DONE) {
                IMAGE_DONE.remove(url);
            }
            return false;
        }
    }

    private static void warmCache(Context context, String url) throws Exception {
        DataSource.Factory factory = VideoCacheHelper.buildCacheDataSourceFactory(context);
        DataSource dataSource = factory.createDataSource();
        // ExoPlayer 2.10: (uri, absoluteStreamPosition, length, key)
        DataSpec dataSpec = new DataSpec(Uri.parse(url), 0, PRELOAD_BYTES, null);
        try {
            dataSource.open(dataSpec);
            byte[] buffer = new byte[64 * 1024];
            long total = 0;
            while (total < PRELOAD_BYTES) {
                int read = dataSource.read(buffer, 0, buffer.length);
                if (read == -1) {
                    break;
                }
                total += read;
            }
        } finally {
            try {
                dataSource.close();
            } catch (Exception ignored) {
            }
        }
    }
}
