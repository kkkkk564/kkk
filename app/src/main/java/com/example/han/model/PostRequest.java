package com.example.han.model;

public class PostRequest {
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String type;

    public PostRequest(String title, String description, String content, String imageUrl, String type) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
}
