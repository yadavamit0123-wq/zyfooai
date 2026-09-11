package com.growwthapps.dailypost.v2.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.growwthapps.dailypost.v2.api.ApiStatus;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.model.UserItem;
import com.growwthapps.dailypost.v2.respository.UserRespository;

public class UserViewModel extends ViewModel {


    UserRespository respository;

    public UserViewModel() {
        this.respository = new UserRespository();
    }

    public LiveData<ApiStatus> contactUsMessage(String uid, String name, String email, String number, String message) {
        return respository.contactUsMessage(uid, name, email, number, message);

    }

    public LiveData<UserItem> storeTransaction(String device_id, String plan_id, String amount, String transaction_id) {

        return respository.storeTransaction(device_id, plan_id, amount, transaction_id);
    }

}

