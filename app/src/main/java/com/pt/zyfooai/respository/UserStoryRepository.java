package com.pt.zyfooai.respository;

import android.content.Context;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

        List<MultipartBody.Part> parts = new ArrayList<>();
        int count = Math.min(imageUris.size(), MAX_IMAGES);
        for (int i = 0; i < count; i++) {
            MultipartBody.Part part = uriToPart(context, imageUris.get(i), i);
            if (part != null) {
                parts.add(part);
            }
        }
        if (parts.isEmpty()) {
            UserStoryDetailResponse error = new UserStoryDetailResponse();
            error.success = false;
            error.message = "Could not read images";
            liveData.setValue(error);
            return liveData;
        }

        apiService.createUserStory(parts).enqueue(new Callback<UserStoryDetailResponse>() {
            @Override
            public void onResponse(Call<UserStoryDetailResponse> call, Response<UserStoryDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    UserStoryDetailResponse error = new UserStoryDetailResponse();
                    error.success = false;
                    error.message = "Upload failed (" + response.code() + ")";
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

    @Nullable
    private static MultipartBody.Part uriToPart(Context context, Uri uri, int index) {
        if (uri == null) {
            return null;
        }
        try {
            File cacheFile = new File(context.getCacheDir(), "story_upload_" + index + "_" + System.currentTimeMillis() + ".jpg");
            try (InputStream in = context.getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(cacheFile)) {
                if (in == null) {
                    return null;
                }
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), cacheFile);
            return MultipartBody.Part.createFormData("images[]", cacheFile.getName(), body);
        } catch (Exception e) {
            Log.d(TAG, "uriToPart failed: " + e.getMessage());
            return null;
        }
    }
}
