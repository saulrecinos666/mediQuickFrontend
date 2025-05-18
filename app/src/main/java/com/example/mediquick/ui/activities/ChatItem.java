package com.example.mediquick.ui.activities;
public class ChatItem {
    private String name;
    private int imageResId;

    public ChatItem(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
