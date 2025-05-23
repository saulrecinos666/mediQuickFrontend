package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

public class MessageUser {
    @SerializedName("user_first_name")
    public String firstName;

    @SerializedName("user_second_name")
    public String secondName;

    @SerializedName("user_first_lastname")
    public String firstLastName;

    @SerializedName("user_second_lastname")
    public String secondLastName;

    @SerializedName("user_email")
    public String email;
}
