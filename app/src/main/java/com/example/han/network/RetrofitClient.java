package com.example.han.network;

import android.content.Context;

import com.example.han.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static volatile ApiService apiService;
    private static volatile ApiService apiServiceNoAuth;

    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            synchronized (RetrofitClient.class) {
                if (apiService == null) {
                    TokenManager tokenManager = TokenManager.getInstance(context);
                    OkHttpClient client = createOkHttpClient(context, tokenManager);
                    apiService = createRetrofit(client).create(ApiService.class);
                }
            }
        }
        return apiService;
    }

    public static ApiService getApiServiceNoAuth(Context context) {
        if (apiServiceNoAuth == null) {
            synchronized (RetrofitClient.class) {
                if (apiServiceNoAuth == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .addInterceptor(createLoggingInterceptor())
                            .build();
                    apiServiceNoAuth = createRetrofit(client).create(ApiService.class);
                }
            }
        }
        return apiServiceNoAuth;
    }

    private static OkHttpClient createOkHttpClient(Context context, TokenManager tokenManager) {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(createLoggingInterceptor())
                .addInterceptor(new AuthInterceptor(tokenManager))
                .authenticator(new AuthAuthenticator(tokenManager, context))
                .build();
    }

    private static HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    private static Retrofit createRetrofit(OkHttpClient client) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
