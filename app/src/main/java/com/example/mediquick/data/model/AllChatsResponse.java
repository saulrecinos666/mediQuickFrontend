package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AllChatsResponse {
    public boolean success;
    public int code;
    public String message;
    public List<ChatData> data;
    public String timestamp;

    public static class ChatData {
        @SerializedName("chat_id")
        public int chatId;

        @SerializedName("chat_user_id")
        public String chatUserId;

        @SerializedName("chat_user")
        public User chatUser;

        @SerializedName("chat_doctor_id")
        public String chatDoctorId;

        @SerializedName("chat_doctor")
        public User chatDoctor;

        @SerializedName("chat_updated_at")
        public String chatUpdatedAt;

        public Message message;
    }

    public static class Message {
        @SerializedName("message_id")
        public int messageId;

        @SerializedName("message_content")
        public String content;

        @SerializedName("message_created_at")
        public String createdAt;

        @SerializedName("message_file_type")
        public String fileType;

        @SerializedName("message_file_path")
        public String filePath;
    }

    public static class User {
        @SerializedName("user_id")
        public String userId;

        @SerializedName("user_first_name")
        public String firstName;

        @SerializedName("user_second_name")
        public String secondName;

        @SerializedName("user_third_name")
        public String thirdName;

        @SerializedName("user_first_lastname")
        public String firstLastname;

        @SerializedName("user_second_lastname")
        public String secondLastname;

        @SerializedName("user_third_lastname")
        public String thirdLastname;

        @SerializedName("user_email")
        public String email;

        @SerializedName("user_phone_number")
        public String phone;

        @SerializedName("user_address")
        public String address;

        @SerializedName("user_gender")
        public String gender;

        @SerializedName("user_state_id")
        public int stateId;
    }
}
