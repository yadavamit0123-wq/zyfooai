package com.pt.zyfooai.respository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pt.zyfooai.api.ApiClient;
import com.pt.zyfooai.api.ApiService;
import com.pt.zyfooai.model.ApplyReferralRequest;
import com.pt.zyfooai.model.ApplyReferralResponse;
import com.pt.zyfooai.model.CheckReferralResponse;
import com.pt.zyfooai.model.FaqListResponse;
import com.pt.zyfooai.model.ReferralListResponse;
import com.pt.zyfooai.model.ReferralMeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ReferralRepository {

    private static final String TAG = "ReferralRepository";
    private final ApiService api = ApiClient.getApiDataService();

    public LiveData<CheckReferralResponse> checkCode(String code, String userId) {
        MutableLiveData<CheckReferralResponse> live = new MutableLiveData<>();
        api.checkReferralCode(code, userId).enqueue(new Callback<CheckReferralResponse>() {
            @Override
            public void onResponse(Call<CheckReferralResponse> call, Response<CheckReferralResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    live.setValue(response.body());
                } else {
                    CheckReferralResponse err = new CheckReferralResponse();
                    err.validRaw = false;
                    err.message = "Invalid referral code";
                    live.setValue(err);
                }
            }

            @Override
            public void onFailure(Call<CheckReferralResponse> call, Throwable t) {
                Log.d(TAG, "checkCode: " + t.getMessage());
                CheckReferralResponse err = new CheckReferralResponse();
                err.validRaw = false;
                err.message = t.getMessage() != null ? t.getMessage() : "Network error";
                live.setValue(err);
            }
        });
        return live;
    }

    public LiveData<ApplyReferralResponse> apply(String userId, String code, String deviceId) {
        MutableLiveData<ApplyReferralResponse> live = new MutableLiveData<>();
        api.applyReferral(new ApplyReferralRequest(userId, code, deviceId))
                .enqueue(new Callback<ApplyReferralResponse>() {
                    @Override
                    public void onResponse(Call<ApplyReferralResponse> call, Response<ApplyReferralResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            live.setValue(response.body());
                        } else {
                            ApplyReferralResponse err = new ApplyReferralResponse();
                            err.successRaw = false;
                            err.message = "Could not apply referral code";
                            live.setValue(err);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApplyReferralResponse> call, Throwable t) {
                        Log.d(TAG, "apply: " + t.getMessage());
                        ApplyReferralResponse err = new ApplyReferralResponse();
                        err.successRaw = false;
                        err.message = t.getMessage() != null ? t.getMessage() : "Network error";
                        live.setValue(err);
                    }
                });
        return live;
    }

    public LiveData<ReferralMeResponse> getMe(String userId) {
        MutableLiveData<ReferralMeResponse> live = new MutableLiveData<>();
        api.getReferralMe(userId).enqueue(new Callback<ReferralMeResponse>() {
            @Override
            public void onResponse(Call<ReferralMeResponse> call, Response<ReferralMeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    live.setValue(response.body().resolved());
                } else {
                    live.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ReferralMeResponse> call, Throwable t) {
                Log.d(TAG, "getMe: " + t.getMessage());
                live.setValue(null);
            }
        });
        return live;
    }

    public LiveData<ReferralListResponse> getList(String userId, int page) {
        MutableLiveData<ReferralListResponse> live = new MutableLiveData<>();
        api.getReferralList(userId, page).enqueue(new Callback<ReferralListResponse>() {
            @Override
            public void onResponse(Call<ReferralListResponse> call, Response<ReferralListResponse> response) {
                if (response.isSuccessful()) {
                    live.setValue(response.body());
                } else {
                    live.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ReferralListResponse> call, Throwable t) {
                Log.d(TAG, "getList: " + t.getMessage());
                live.setValue(null);
            }
        });
        return live;
    }

    public LiveData<FaqListResponse> getFaq() {
        MutableLiveData<FaqListResponse> live = new MutableLiveData<>();
        api.getFaq("Referral").enqueue(new Callback<FaqListResponse>() {
            @Override
            public void onResponse(Call<FaqListResponse> call, Response<FaqListResponse> response) {
                if (response.isSuccessful()) {
                    live.setValue(response.body());
                } else {
                    // Fallback spelling used by some admin panels
                    api.getFaq("Refferal").enqueue(new Callback<FaqListResponse>() {
                        @Override
                        public void onResponse(Call<FaqListResponse> call2, Response<FaqListResponse> response2) {
                            live.setValue(response2.isSuccessful() ? response2.body() : null);
                        }

                        @Override
                        public void onFailure(Call<FaqListResponse> call2, Throwable t) {
                            live.setValue(null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<FaqListResponse> call, Throwable t) {
                Log.d(TAG, "getFaq: " + t.getMessage());
                live.setValue(null);
            }
        });
        return live;
    }
}
