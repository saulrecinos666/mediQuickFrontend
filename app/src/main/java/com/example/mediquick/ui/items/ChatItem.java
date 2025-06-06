package com.example.mediquick.ui.items;

public class ChatItem {
    private String name;
    private int imageResId;
    private String lastMessage;
    private String senderLabel;
    private String userId;
    private int chatId;


    public ChatItem(String name, int imageResId, String lastMessage, String senderLabel, String userId, int chatId) {
        this.name = name;
        this.imageResId = imageResId;
        this.lastMessage = lastMessage;
        this.senderLabel = senderLabel;
        this.userId = userId;
        this.chatId = chatId;

    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getSenderLabel() {
        return senderLabel;
    }

    public String getUserId() {
        return userId;
    }
    public int getChatId() {
        return chatId;
    }

}
