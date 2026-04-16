package com.example.han.network;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.han.util.Constants;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static volatile TokenManager instance;
    private SharedPreferences sharedPreferences;

    private TokenManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    Constants.PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static TokenManager getInstance(Context context) {
        if (instance == null) {
            synchronized (TokenManager.class) {
                if (instance == null) {
                    instance = new TokenManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(Constants.KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.KEY_TOKEN, null);
    }

    public void saveRefreshToken(String refreshToken) {
        sharedPreferences.edit().putString(Constants.KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(Constants.KEY_REFRESH_TOKEN, null);
    }

    public void saveUserInfo(long userId, String userName) {
        sharedPreferences.edit()
                .putLong(Constants.KEY_USER_ID, userId)
                .putString(Constants.KEY_USER_NAME, userName)
                .apply();
    }

    public long getUserId() {
        return sharedPreferences.getLong(Constants.KEY_USER_ID, -1);
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, "");
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }
}
