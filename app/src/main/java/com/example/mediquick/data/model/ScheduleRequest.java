package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduleRequest {

    @SerializedName("doctorUserId")
    private String doctorUserId;

    @SerializedName("medicalAppointmentDateTime")
    private String medicalAppointmentDateTime;

    // Constructor vacío
    public ScheduleRequest() {}

    // Constructor con parámetros
    public ScheduleRequest(String doctorUserId, String medicalAppointmentDateTime) {
        this.doctorUserId = doctorUserId;
        this.medicalAppointmentDateTime = medicalAppointmentDateTime;
    }

    // Getters y Setters
    public String getDoctorUserId() {
        return doctorUserId;
    }

    public void setDoctorUserId(String doctorUserId) {
        this.doctorUserId = doctorUserId;
    }

    public String getMedicalAppointmentDateTime() {
        return medicalAppointmentDateTime;
    }

    public void setMedicalAppointmentDateTime(String medicalAppointmentDateTime) {
        this.medicalAppointmentDateTime = medicalAppointmentDateTime;
    }
}
