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
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    String apiKey = new PreferenceManager(MyApplication.getAppContext()).getString(Constant.API_KEY);
                    if (apiKey.equals("")){
                        apiKey = "1234567";
                    }
                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", apiKey)
                            .header("Content-Type", "text/plain")
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);

                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount < 3) {

                        Log.d("intercept", "Request is not successful - " + tryCount);

                        tryCount++;

                        // retry the request
                        response.close();
                        response = chain.proceed(original);
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
