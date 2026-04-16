package com.example.han.network;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.han.activity.LoginActivity;
import com.example.han.model.ApiResponse;
import com.example.han.model.RefreshData;
import com.example.han.model.RefreshRequest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class AuthAuthenticator implements Authenticator {
    private final TokenManager tokenManager;
    private final Context context;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    public AuthAuthenticator(TokenManager tokenManager, Context context) {
        this.tokenManager = tokenManager;
        this.context = context.getApplicationContext();
    }

    @Override
    public Request authenticate(Route route, Response response) {
        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            logoutAndRedirect();
            return null;
        }

        if (isRefreshing.compareAndSet(false, true)) {
            try {
                retrofit2.Response<ApiResponse<RefreshData>> refreshResponse =
                        RetrofitClient.getApiServiceNoAuth(context).refreshToken(
                                new RefreshRequest(refreshToken)).execute();

                if (refreshResponse.isSuccessful() && refreshResponse.body() != null
                        && refreshResponse.body().isSuccess()) {
                    RefreshData data = refreshResponse.body().getData();
                    tokenManager.saveToken(data.getToken());
                    tokenManager.saveRefreshToken(data.getRefreshToken());
                    if (data.getUser() != null) {
                        tokenManager.saveUserInfo(data.getUser().getId(), data.getUser().getName());
                    }
                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + data.getToken())
                            .build();
                } else {
                    logoutAndRedirect();
                    return null;
                }
            } catch (IOException e) {
                logoutAndRedirect();
                return null;
            } finally {
                isRefreshing.set(false);
            }
        } else {
            // Wait for the other refresh to complete, then retry with new token
            try {
                Thread.sleep(500);
                String newToken = tokenManager.getToken();
                if (newToken != null && !newToken.isEmpty()) {
                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + newToken)
                            .build();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    private void logoutAndRedirect() {
        tokenManager.clearAll();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
