package com.example.han.model;

public class PostItem {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String type;
    private String authorName;
    private String authorAvatar;
    private Integer likesCount;
    private Integer commentsCount;
    private String createdAt;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
    public String getAuthorName() { return authorName; }
    public String getAuthorAvatar() { return authorAvatar; }
    public Integer getLikesCount() { return likesCount; }
    public Integer getCommentsCount() { return commentsCount; }
    public String getCreatedAt() { return createdAt; }
}
