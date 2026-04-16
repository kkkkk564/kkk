package com.example.han.model;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public boolean isSuccess() { return code == 0; }
}
