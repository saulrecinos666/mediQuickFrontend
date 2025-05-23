package com.example.mediquick.services;

import com.example.mediquick.data.model.AllChatsResponse;
import com.example.mediquick.data.model.ChatResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ChatService {

    @GET("api/chat/get-chats")
    Call<AllChatsResponse> getAllChats(@Header("Authorization") String token);

    @GET("api/chat/get-chats/{id}")
    Call<ChatResponse> getChatMessages(@Path("id") String chatId);
}

