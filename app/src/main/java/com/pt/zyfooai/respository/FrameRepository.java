package com.pt.zyfooai.respository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pt.zyfooai.api.ApiClient;
import com.pt.zyfooai.api.ApiService;
import com.pt.zyfooai.model.FrameConfig;
import com.pt.zyfooai.model.FrameListResponse;
import com.pt.zyfooai.utils.FrameCache;
import com.pt.zyfooai.utils.FrameCatalogProvider;
import com.pt.zyfooai.utils.FrameMediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fetches published PNG overlay frames from the backend and caches them locally.
 */
public final class FrameRepository {

    private static final String TAG = "FrameRepository";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private FrameRepository() {
    }

    public interface SyncListener {
        void onComplete(boolean updated);
    }

    private interface MediaSyncCallback {
        void onResult(boolean saved);
    }

    public static void syncInBackground(Context context, SyncListener listener) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        EXECUTOR.execute(() -> {
            boolean updated = syncBlocking(appContext);
            if (listener != null) {
                MAIN_HANDLER.post(() -> listener.onComplete(updated));
            }
        });
    }

    public static boolean syncBlocking(Context context) {
        if (context == null) {
            return false;
        }
        ApiService apiService = ApiClient.getApiDataService();
        boolean updated = false;
        updated |= syncMediaType(context, apiService, FrameMediaType.IMAGE);
        updated |= syncMediaType(context, apiService, FrameMediaType.REELS);
        if (updated) {
            FrameCatalogProvider.invalidate();
        }
        return updated;
    }

    private static boolean syncMediaType(Context context, ApiService apiService, FrameMediaType mediaType) {
        String queryType = mediaType == FrameMediaType.REELS ? "reels" : "image";
        try {
            Response<FrameListResponse> response = apiService
                    .getFrames(queryType, "published")
                    .execute();
            if (!response.isSuccessful() || response.body() == null) {
                Log.d(TAG, "Frame sync skipped for " + queryType + " — HTTP " + response.code());
                return false;
            }
            FrameListResponse body = response.body();
            List<FrameConfig> frames = body.data != null ? body.data : new ArrayList<>();
            List<FrameConfig> published = FrameCache.publishedFrames(frames);
            FrameCache.save(context, mediaType, published);
            if (body.version > 0 || (body.lastUpdated != null && !body.lastUpdated.isEmpty())) {
                FrameCache.saveMeta(context, body.version, body.lastUpdated);
            }
            Log.d(TAG, "Synced " + published.size() + " " + queryType + " frame(s) from server");
            return true;
        } catch (Exception error) {
            Log.d(TAG, "Frame sync failed for " + queryType + ": " + error.getMessage());
            return false;
        }
    }

    public static void syncAsync(Context context, SyncListener listener) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        ApiService apiService = ApiClient.getApiDataService();
        final boolean[] updated = {false};
        syncMediaTypeAsync(appContext, apiService, FrameMediaType.IMAGE, success -> {
            updated[0] |= success;
            syncMediaTypeAsync(appContext, apiService, FrameMediaType.REELS, reelsSuccess -> {
                updated[0] |= reelsSuccess;
                if (updated[0]) {
                    FrameCatalogProvider.invalidate();
                }
                MAIN_HANDLER.post(() -> {
                    if (listener != null) {
                        listener.onComplete(updated[0]);
                    }
                });
            });
        });
    }

    private static void syncMediaTypeAsync(
            Context context,
            ApiService apiService,
            FrameMediaType mediaType,
            MediaSyncCallback onDone
    ) {
        String queryType = mediaType == FrameMediaType.REELS ? "reels" : "image";
        apiService.getFrames(queryType, "published").enqueue(new Callback<FrameListResponse>() {
            @Override
            public void onResponse(Call<FrameListResponse> call, Response<FrameListResponse> response) {
                boolean saved = false;
                if (response.isSuccessful() && response.body() != null) {
                    FrameListResponse body = response.body();
                    List<FrameConfig> frames = body.data != null ? body.data : new ArrayList<>();
                    FrameCache.save(context, mediaType, FrameCache.publishedFrames(frames));
                    if (body.version > 0 || (body.lastUpdated != null && !body.lastUpdated.isEmpty())) {
                        FrameCache.saveMeta(context, body.version, body.lastUpdated);
                    }
                    saved = true;
                }
                if (onDone != null) {
                    onDone.onResult(saved);
                }
            }

            @Override
            public void onFailure(Call<FrameListResponse> call, Throwable t) {
                Log.d(TAG, "Async frame sync failed for " + queryType + ": " + t.getMessage());
                if (onDone != null) {
                    onDone.onResult(false);
                }
            }
        });
    }
}
