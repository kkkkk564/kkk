package com.example.han.model;

public class CommentRequest {
    private long postId;
    private String content;

    public CommentRequest(long postId, String content) {
        this.postId = postId;
        this.content = content;
    }

    public long getPostId() { return postId; }
    public String getContent() { return content; }
}
