// CreateAppointmentRequest.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateAppointmentRequest {

    @SerializedName("medicalProcedureId")
    private String medicalProcedureId;

    @SerializedName("branchId")
    private String branchId;

    // Constructor vacío
    public CreateAppointmentRequest() {}

    // Constructor con parámetros
    public CreateAppointmentRequest(String medicalProcedureId, String branchId) {
        this.medicalProcedureId = medicalProcedureId;
        this.branchId = branchId;
    }

    // Getters y Setters
    public String getMedicalProcedureId() {
        return medicalProcedureId;
    }

    public void setMedicalProcedureId(String medicalProcedureId) {
        this.medicalProcedureId = medicalProcedureId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    // Método utilitario para validación
    public boolean isValid() {
        return medicalProcedureId != null && !medicalProcedureId.trim().isEmpty() &&
                branchId != null && !branchId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "CreateAppointmentRequest{" +
                "medicalProcedureId='" + medicalProcedureId + '\'' +
                ", branchId='" + branchId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CreateAppointmentRequest that = (CreateAppointmentRequest) obj;
        return (medicalProcedureId != null ? medicalProcedureId.equals(that.medicalProcedureId) : that.medicalProcedureId == null) &&
                (branchId != null ? branchId.equals(that.branchId) : that.branchId == null);
    }

    @Override
    public int hashCode() {
        int result = medicalProcedureId != null ? medicalProcedureId.hashCode() : 0;
        result = 31 * result + (branchId != null ? branchId.hashCode() : 0);
        return result;
    }
}