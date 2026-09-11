package com.growwthapps.dailypost.v2.respository;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("status")
    public boolean status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public T data; // Generic type to handle different types of responses
}
