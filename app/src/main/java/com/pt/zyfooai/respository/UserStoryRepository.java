package com.pt.zyfooai.respository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pt.zyfooai.api.ApiClient;
import com.pt.zyfooai.api.ApiService;
import com.pt.zyfooai.model.UserStoryDetailResponse;
import com.pt.zyfooai.model.UserStoryFeedItem;
import com.pt.zyfooai.model.UserStoryListResponse;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User Stories API — separate from festival {@code getFestival}.
 */
public final class UserStoryRepository {

    private static final String TAG = "UserStoryRepository";
    private static final int MAX_IMAGES = 10;
    private static final int MAX_EDGE_PX = 1280;
    private static final int JPEG_QUALITY = 78;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final ApiService apiService = ApiClient.getApiDataService();
    private final MutableLiveData<List<UserStoryFeedItem>> feedLiveData = new MutableLiveData<>();

    /** Observe once from UI; call {@link #refreshFeed()} to reload. */
    public LiveData<List<UserStoryFeedItem>> observeFeed() {
        return feedLiveData;
    }

    public void refreshFeed() {
        apiService.getUserStories().enqueue(new Callback<UserStoryListResponse>() {
            @Override
            public void onResponse(Call<UserStoryListResponse> call, Response<UserStoryListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    feedLiveData.setValue(response.body().data);
                } else {
                    Log.d(TAG, "getFeed HTTP " + response.code());
                    feedLiveData.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<UserStoryListResponse> call, Throwable t) {
                Log.d(TAG, "getFeed failed: " + t.getMessage());
                feedLiveData.setValue(new ArrayList<>());
            }
        });
    }

    /** @deprecated use {@link #observeFeed()} + {@link #refreshFeed()} */
    public LiveData<List<UserStoryFeedItem>> getFeed() {
        refreshFeed();
        return feedLiveData;
    }

    public LiveData<UserStoryDetailResponse> getDetail(String storyId) {
        MutableLiveData<UserStoryDetailResponse> liveData = new MutableLiveData<>();
        apiService.getUserStory(storyId).enqueue(new Callback<UserStoryDetailResponse>() {
            @Override
            public void onResponse(Call<UserStoryDetailResponse> call, Response<UserStoryDetailResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    UserStoryDetailResponse error = new UserStoryDetailResponse();
                    error.success = false;
                    error.code = response.code() == 404 ? "EXPIRED" : "ERROR";
                    liveData.setValue(error);
                }
            }

            @Override
            public void onFailure(Call<UserStoryDetailResponse> call, Throwable t) {
                UserStoryDetailResponse error = new UserStoryDetailResponse();
                error.success = false;
                error.message = t.getMessage();
                liveData.setValue(error);
            }
        });
        return liveData;
    }

    public LiveData<UserStoryDetailResponse> createStory(Context context, List<Uri> imageUris) {
        MutableLiveData<UserStoryDetailResponse> liveData = new MutableLiveData<>();
        if (context == null || imageUris == null || imageUris.isEmpty()) {
            UserStoryDetailResponse error = new UserStoryDetailResponse();
            error.success = false;
            error.message = "No images";
            liveData.setValue(error);
            return liveData;
        }

        Context appContext = context.getApplicationContext();
        EXECUTOR.execute(() -> {
            List<MultipartBody.Part> parts = new ArrayList<>();
            int count = Math.min(imageUris.size(), MAX_IMAGES);
            for (int i = 0; i < count; i++) {
                MultipartBody.Part part = uriToCompressedPart(appContext, imageUris.get(i), i);
                if (part != null) {
                    parts.add(part);
                }
            }
            if (parts.isEmpty()) {
                UserStoryDetailResponse error = new UserStoryDetailResponse();
                error.success = false;
                error.message = "Could not read images";
                liveData.postValue(error);
                return;
            }

            String userId = new PreferenceManager(appContext).getString(Constant.USER_ID);
            if (userId == null) {
                userId = "";
            }
            RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId);

            apiService.createUserStory(userIdBody, parts).enqueue(new Callback<UserStoryDetailResponse>() {
                @Override
                public void onResponse(Call<UserStoryDetailResponse> call, Response<UserStoryDetailResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        liveData.postValue(response.body());
                        return;
                    }
                    UserStoryDetailResponse error = new UserStoryDetailResponse();
                    error.success = false;
                    String bodyHint = "";
                    try {
                        if (response.errorBody() != null) {
                            bodyHint = response.errorBody().string();
                            if (bodyHint.length() > 180) {
                                bodyHint = bodyHint.substring(0, 180);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                    Log.e(TAG, "createStory HTTP " + response.code() + " " + bodyHint);
                    if (response.code() == 401) {
                        error.message = "Auth failed (401). Backend ko same API key / Bearer allow karna hoga.";
                    } else {
                        error.message = "Upload failed (" + response.code() + ")";
                    }
                    liveData.postValue(error);
                }

                @Override
                public void onFailure(Call<UserStoryDetailResponse> call, Throwable t) {
                    UserStoryDetailResponse error = new UserStoryDetailResponse();
                    error.success = false;
                    error.message = t.getMessage();
                    liveData.postValue(error);
                }
            });
        });
        return liveData;
    }

    public void markSeen(String storyId) {
        if (storyId == null || storyId.isEmpty()) {
            return;
        }
        apiService.markUserStorySeen(storyId).enqueue(new Callback<UserStoryDetailResponse>() {
            @Override
            public void onResponse(Call<UserStoryDetailResponse> call, Response<UserStoryDetailResponse> response) {
                // best-effort
            }

            @Override
            public void onFailure(Call<UserStoryDetailResponse> call, Throwable t) {
                // best-effort
            }
        });
    }

    /**
     * Compress to JPEG ≤1280px for faster upload (stories don't need full camera resolution).
     * Part name {@code images[]} per API contract; also works with Laravel array uploads.
     */
    @Nullable
    private static MultipartBody.Part uriToCompressedPart(Context context, Uri uri, int index) {
        if (uri == null) {
            return null;
        }
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                if (in == null) {
                    return null;
                }
                BitmapFactory.decodeStream(in, null, bounds);
            }

            int sample = 1;
            int maxDim = Math.max(bounds.outWidth, bounds.outHeight);
            while (maxDim / sample > MAX_EDGE_PX * 2) {
                sample *= 2;
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = Math.max(1, sample);
            Bitmap bitmap;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                if (in == null) {
                    return null;
                }
                bitmap = BitmapFactory.decodeStream(in, null, opts);
            }
            if (bitmap == null) {
                return null;
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            float scale = Math.min(1f, MAX_EDGE_PX / (float) Math.max(w, h));
            if (scale < 1f) {
                Bitmap scaled = Bitmap.createScaledBitmap(
                        bitmap,
                        Math.max(1, Math.round(w * scale)),
                        Math.max(1, Math.round(h * scale)),
                        true
                );
                if (scaled != bitmap) {
                    bitmap.recycle();
                    bitmap = scaled;
                }
            }

            File cacheFile = new File(
                    context.getCacheDir(),
                    "story_upload_" + index + "_" + System.currentTimeMillis() + ".jpg"
            );
            try (FileOutputStream out = new FileOutputStream(cacheFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
            }
            bitmap.recycle();

            RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), cacheFile);
            return MultipartBody.Part.createFormData("images[]", cacheFile.getName(), body);
        } catch (Exception e) {
            Log.d(TAG, "uriToCompressedPart failed: " + e.getMessage());
            return null;
        }
    }
}
