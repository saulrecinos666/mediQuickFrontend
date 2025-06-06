package com.example.mediquick.services;

import com.example.mediquick.data.model.AllDoctorsResponse;
import com.example.mediquick.data.model.AppointmentResponse;
import com.example.mediquick.data.model.GetAppointmentResponse;
import com.example.mediquick.data.model.GetAssignedAppointmentsResponse;
import com.example.mediquick.data.model.PrescriptionRequest;
import com.example.mediquick.data.model.ResponsePrescription;
import com.example.mediquick.data.model.ScheduleRequest;
import com.example.mediquick.data.model.ScheduleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppointmentService {
    @GET("api/appointments/status/{status}") // ✅ Obtener todas las citas
    Call<AppointmentResponse> getAllAppointments(@Path("status") String statusId);


    @GET("api/users/all-doctors") // Listar todos los doctores
    Call<AllDoctorsResponse> getAllDoctors();

    @GET("api/appointments/filter")
    Call<GetAssignedAppointmentsResponse> getAssignedAppointments(@Query("doctorUserId") String doctorUserId);

    @GET("api/appointments/{appointmentId}")
    Call<GetAppointmentResponse> getAppointmentById(@Path("appointmentId") String appointmentId);

    @PATCH("api/appointments/schedule/{medicalAppointmentId}")
    Call<ScheduleResponse> scheduleAppointment(
            @Path("medicalAppointmentId") String appointmentId,
            @Body ScheduleRequest request
    );

    @POST("api/prescriptions")
    Call<ResponsePrescription> createPrescription(@Body PrescriptionRequest prescription);
}