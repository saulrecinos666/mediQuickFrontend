package com.example.mediquick.services;

import com.example.mediquick.data.model.GetUserDataResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AuthService {

    @GET("api/users/{userId}")
    Call<GetUserDataResponse> getUserDataResponseCall(@Path("userId") String userId);

}
