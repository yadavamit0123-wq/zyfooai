package com.pt.zyfooai.utils;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.pt.zyfooai.R;

import java.io.File;

public final class VideoCacheHelper {

    private static SimpleCache simpleCache;
    private static final long MAX_CACHE_BYTES = 100L * 1024L * 1024L;

    private VideoCacheHelper() {
    }

    public static synchronized SimpleCache getCache(Context context) {
        if (simpleCache == null) {
            File cacheDir = new File(context.getApplicationContext().getCacheDir(), AppConstants.VIDEO_CACHE_DIR);
            simpleCache = new SimpleCache(cacheDir, new LeastRecentlyUsedCacheEvictor(MAX_CACHE_BYTES));
        }
        return simpleCache;
    }

    public static DataSource.Factory buildCacheDataSourceFactory(Context context) {
        Context appContext = context.getApplicationContext();
        String userAgent = Util.getUserAgent(appContext, appContext.getString(R.string.app_name));
        DefaultDataSourceFactory upstream = new DefaultDataSourceFactory(appContext, userAgent);
        return new CacheDataSourceFactory(
                getCache(appContext),
                upstream,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        );
    }
}
