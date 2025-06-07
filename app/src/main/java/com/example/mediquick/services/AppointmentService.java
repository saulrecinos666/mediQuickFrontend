package com.example.mediquick.services;

import com.example.mediquick.data.model.AllDoctorsResponse;
import com.example.mediquick.data.model.AppointmentResponse;
import com.example.mediquick.data.model.CreateAppointmentRequest;
import com.example.mediquick.data.model.CreateAppointmentResponse;
import com.example.mediquick.data.model.GetAllBranchesByInstitutionResponse;
import com.example.mediquick.data.model.GetAllInstitutionsResponse;
import com.example.mediquick.data.model.GetAllProceduresByBranchIdResponse;
import com.example.mediquick.data.model.GetAppointmentResponse;
import com.example.mediquick.data.model.GetAssignedAppointmentsResponse;
import com.example.mediquick.data.model.PrescriptionRequest;
import com.example.mediquick.data.model.ResponsePrescription;
import com.example.mediquick.data.model.ScheduleRequest;
import com.example.mediquick.data.model.ScheduleResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppointmentService {
    @GET("api/appointments/status/{status}")
    Call<AppointmentResponse> getAllAppointments(@Path("status") String statusId);

    @GET("api/users/all-doctors")
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

    @GET("api/institutions")
    Call<GetAllInstitutionsResponse> getAllInstitutions();

    @GET("api/branches")
    Call<GetAllBranchesByInstitutionResponse> getAllBranchesByInstitution(@Query("institutionId") String institutionId);

    @GET("api/procedure")
    Call<GetAllProceduresByBranchIdResponse> getAllProceduresByBranchId(@Query("branchId") String branchId);

    @POST("api/appointments")
    @Multipart
    Call<CreateAppointmentResponse> createAppointment(
            @Part("branchId") RequestBody branchId,
            @Part("medicalProcedureId") RequestBody medicalProcedureId
    );
}