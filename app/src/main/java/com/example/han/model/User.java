package com.example.han.model;

public class User {
    private long id;
    private String name;
    private String avatar;
    private String bio;

    public long getId() { return id; }
    public String getName() { return name; }
    public String getAvatar() { return avatar; }
    public String getBio() { return bio; }

    public void setName(String name) { this.name = name; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setBio(String bio) { this.bio = bio; }
}
