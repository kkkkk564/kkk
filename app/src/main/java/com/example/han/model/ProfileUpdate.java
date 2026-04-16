package com.example.han.model;

public class ProfileUpdate {
    private String avatar;
    private String bio;

    public ProfileUpdate(String avatar, String bio) {
        this.avatar = avatar;
        this.bio = bio;
    }

    public String getAvatar() { return avatar; }
    public String getBio() { return bio; }
}
