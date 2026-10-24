package com.pt.zyfooai.respository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pt.zyfooai.api.ApiClient;
import com.pt.zyfooai.api.ApiService;
import com.pt.zyfooai.model.ContentReportRequest;
import com.pt.zyfooai.model.ContentReportResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ContentReportRepository {

    private static final String TAG = "ContentReportRepo";
    private final ApiService api = ApiClient.getApiDataService();

    public LiveData<ContentReportResponse> submit(ContentReportRequest request) {
        MutableLiveData<ContentReportResponse> live = new MutableLiveData<>();
        api.submitContentReport(request).enqueue(new Callback<ContentReportResponse>() {
            @Override
            public void onResponse(Call<ContentReportResponse> call, Response<ContentReportResponse> response) {
                ContentReportResponse body = response.body();
                if (response.isSuccessful() && body != null) {
                    body.httpCode = response.code();
                    live.setValue(body);
                    return;
                }
                ContentReportResponse err = new ContentReportResponse();
                err.successRaw = false;
                err.httpCode = response.code();
                err.message = resolveErrorMessage(response);
                live.setValue(err);
            }

            @Override
            public void onFailure(Call<ContentReportResponse> call, Throwable t) {
                Log.d(TAG, "submit: " + t.getMessage());
                ContentReportResponse err = new ContentReportResponse();
                err.successRaw = false;
                err.httpCode = 0;
                err.message = t.getMessage() != null ? t.getMessage() : "Network error";
                live.setValue(err);
            }
        });
        return live;
    }

    private static String resolveErrorMessage(Response<ContentReportResponse> response) {
        if (response.code() == 401) {
            return "Please login again to report content";
        }
        if (response.code() == 429) {
            return "You already reported this post. Try again after 24 hours.";
        }
        if (response.code() == 422) {
            return "Please check email and reason (min 10 characters)";
        }
        if (response.errorBody() != null) {
            try {
                String raw = response.errorBody().string();
                if (raw != null && !raw.trim().isEmpty()) {
                    return raw;
                }
            } catch (IOException ignored) {
            }
        }
        return "Could not submit report";
    }
}
