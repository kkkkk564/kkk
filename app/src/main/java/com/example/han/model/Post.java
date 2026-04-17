package com.example.han.model;

public class Post {
    private long id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String type;
    private String authorName;
    private String authorAvatar;
    private int likesCount;
    private int commentsCount;
    private boolean isLiked;
    private String createdAt;

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
    public String getAuthorName() { return authorName; }
    public String getAuthorAvatar() { return authorAvatar; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
    public boolean isLiked() { return isLiked; }
    public String getCreatedAt() { return createdAt; }
}
