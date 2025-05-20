package com.example.mediquick.services;


import com.example.mediquick.data.model.DeviceRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeviceService {
    @POST("/api/devices")
    Call<Void> registerDevice(@Body DeviceRequest request);
}
