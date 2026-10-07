package com.pt.zyfooai.api;

import android.util.Log;

import com.pt.zyfooai.MyApplication;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static String App_URl = "https://home.zyfooai.com/";

    public static ApiService getApiDataService() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    String apiKey = new PreferenceManager(MyApplication.getAppContext()).getString(Constant.API_KEY);
                    if (apiKey == null || apiKey.equals("")) {
                        apiKey = "1234567";
                    }
                    // Same Authorization as frames/legacy (raw API key from prefs).
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", apiKey)
                            .method(original.method(), original.body());
                    // Multipart must keep its own boundary Content-Type (profile/story uploads).
                    if (!(original.body() instanceof okhttp3.MultipartBody)) {
                        requestBuilder.header("Content-Type", "text/plain");
                    }
                    Request request = requestBuilder.build();

                    Response response = chain.proceed(request);

                    // Do NOT retry auth failures — and always retry with headers (not bare original).
                    int tryCount = 0;
                    while (!response.isSuccessful()
                            && response.code() != 401
                            && response.code() != 403
                            && tryCount < 2) {
                        Log.d("intercept", "Retry " + tryCount + " HTTP " + response.code());
                        tryCount++;
                        response.close();
                        response = chain.proceed(request);
                    }

                    return response;
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(App_URl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService.class);
    }

}
