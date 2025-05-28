package com.example.mediquick.services;

import com.example.mediquick.data.model.AppointmentResponse;
import com.example.mediquick.data.model.ChatResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AppointmentService {

    @GET("api/appointments/status/{status}")
    Call<AppointmentResponse> getAllAppointments(@Path("status") String statusId);
}
