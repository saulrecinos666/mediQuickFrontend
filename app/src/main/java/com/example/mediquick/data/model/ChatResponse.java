package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatResponse {
    public boolean success;
    public int code;
    public String message;
    public List<ChatMessage> data;
}
