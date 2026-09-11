package com.growwthapps.dailypost.v2.respository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.growwthapps.dailypost.v2.api.ApiClient;
import com.growwthapps.dailypost.v2.api.ApiService;
import com.growwthapps.dailypost.v2.api.ApiStatus;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.model.UserItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRespository {


    private ApiService apiService;

    public UserRespository() {
        apiService = ApiClient.getApiDataService();
    }

    public LiveData<ApiStatus> contactUsMessage(String uid, String name, String email, String number, String message) {
        MutableLiveData<ApiStatus> data = new MutableLiveData<>();
        apiService.contactUsMessage(uid, name, email, number, message).enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<UserItem>> authDestroy(String userId) {

        MutableLiveData<List<UserItem>> data = new MutableLiveData<>();
        apiService.authDestroy(userId).enqueue(new Callback<List<UserItem>>() {
            @Override
            public void onResponse(Call<List<UserItem>> call, Response<List<UserItem>> response) {
                data.setValue(response.body());
                Log.d("getFestival", "" + response.body());
            }

            @Override
            public void onFailure(Call<List<UserItem>> call, Throwable t) {
                data.setValue(null);

            }
        });
        return data;
    }

    public LiveData<UserItem> storeTransaction(String device_id, String plan_id, String amount, String transaction_id) {
        MutableLiveData<UserItem> data = new MutableLiveData<>();

        apiService.storeTransaction(device_id, plan_id, amount, transaction_id).enqueue(new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                data.setValue(response.body());
                Log.d("createTransactions", "createTransactions: "+response.body());

            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                t.printStackTrace();
                data.setValue(null);
                Log.d("createTransactions", "onFailure: "+t);

            }
        });
        return data;
    }

}
