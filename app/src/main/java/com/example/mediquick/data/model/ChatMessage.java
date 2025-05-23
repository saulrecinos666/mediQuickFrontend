package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("message_id")
    public int id;

    @SerializedName("message_user_id")
    public String userId;

    @SerializedName("message_content")
    public String content;

    @SerializedName("message_created_at")
    public String createdAt;

    @SerializedName("message_user")
    public MessageUser user;
}
