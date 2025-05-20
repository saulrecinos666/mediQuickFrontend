package com.example.mediquick.data.model;

import java.util.List;

public class ChatMessageApiResponse {
    private boolean success;
    private int code;
    private String message;
    private List<ChatMessage> data;
    private String timestamp;

    public boolean isSuccess() { return success; }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public List<ChatMessage> getData() { return data; }
    public String getTimestamp() { return timestamp; }
}
