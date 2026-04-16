package com.example.han.model;

import java.util.List;

public class PostPage {
    private List<Post> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    public List<Post> getContent() { return content; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getCurrentPage() { return currentPage; }
}
