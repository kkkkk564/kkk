package com.example.han.model;

public class AuthData {
    private String token;
    private String refreshToken;
    private User user;

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public User getUser() { return user; }
}
