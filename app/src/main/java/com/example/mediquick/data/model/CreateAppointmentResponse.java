// CreateAppointmentResponse.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateAppointmentResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private AppointmentData data; // Solo presente en respuesta exitosa

    @SerializedName("errorDetails")
    private Integer errorDetails; // Solo presente en respuesta de error

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor vacío
    public CreateAppointmentResponse() {}

    // Métodos utilitarios
    public boolean hasAppointmentData() {
        return data != null;
    }

    public boolean hasError() {
        return !success;
    }

    public String getAppointmentId() {
        if (hasAppointmentData()) {
            return data.getMedicalAppointmentId();
        }
        return null;
    }

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AppointmentData getData() {
        return data;
    }

    public void setData(AppointmentData data) {
        this.data = data;
    }

    public Integer getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(Integer errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CreateAppointmentResponse{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", appointmentId='" + getAppointmentId() + '\'' +
                ", errorDetails=" + errorDetails +
                '}';
    }

    // Clase para los datos de la cita creada (solo en respuesta exitosa)
    public static class AppointmentData {

        @SerializedName("medical_appointment_id")
        private String medicalAppointmentId;

        @SerializedName("non_registered_patient_id")
        private String nonRegisteredPatientId;

        @SerializedName("patient_user_id")
        private String patientUserId;

        @SerializedName("doctor_user_id")
        private String doctorUserId;

        @SerializedName("appointment_scheduler_id")
        private String appointmentSchedulerId;

        @SerializedName("branch_id")
        private String branchId;

        @SerializedName("medical_procedure_id")
        private String medicalProcedureId;

        @SerializedName("medical_appointment_state_id")
        private int medicalAppointmentStateId;

        @SerializedName("medical_appointment_date_time")
        private String medicalAppointmentDateTime;

        @SerializedName("medical_appointment_cancellation_reason")
        private String medicalAppointmentCancellationReason;

        @SerializedName("medical_appointment_notes")
        private String medicalAppointmentNotes;

        @SerializedName("medical_appointment_created_at")
        private String medicalAppointmentCreatedAt;

        @SerializedName("medical_appointment_updated_at")
        private String medicalAppointmentUpdatedAt;

        // Constructor vacío
        public AppointmentData() {}

        // Métodos utilitarios
        public boolean hasDoctor() {
            return doctorUserId != null && !doctorUserId.isEmpty();
        }

        public boolean hasScheduler() {
            return appointmentSchedulerId != null && !appointmentSchedulerId.isEmpty();
        }

        public boolean hasDateTime() {
            return medicalAppointmentDateTime != null && !medicalAppointmentDateTime.isEmpty();
        }

        public boolean isNonRegisteredPatient() {
            return nonRegisteredPatientId != null && !nonRegisteredPatientId.isEmpty();
        }

        public String getStateDescription() {
            // Mapear el estado según tu lógica de negocio
            switch (medicalAppointmentStateId) {
                case 1: return "Pendiente";
                case 2: return "Programada";
                case 3: return "Completada";
                case 4: return "Cancelada";
                default: return "Estado desconocido";
            }
        }

        // Getters y Setters
        public String getMedicalAppointmentId() {
            return medicalAppointmentId;
        }

        public void setMedicalAppointmentId(String medicalAppointmentId) {
            this.medicalAppointmentId = medicalAppointmentId;
        }

        public String getNonRegisteredPatientId() {
            return nonRegisteredPatientId;
        }

        public void setNonRegisteredPatientId(String nonRegisteredPatientId) {
            this.nonRegisteredPatientId = nonRegisteredPatientId;
        }

        public String getPatientUserId() {
            return patientUserId;
        }

        public void setPatientUserId(String patientUserId) {
            this.patientUserId = patientUserId;
        }

        public String getDoctorUserId() {
            return doctorUserId;
        }

        public void setDoctorUserId(String doctorUserId) {
            this.doctorUserId = doctorUserId;
        }

        public String getAppointmentSchedulerId() {
            return appointmentSchedulerId;
        }

        public void setAppointmentSchedulerId(String appointmentSchedulerId) {
            this.appointmentSchedulerId = appointmentSchedulerId;
        }

        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }

        public String getMedicalProcedureId() {
            return medicalProcedureId;
        }

        public void setMedicalProcedureId(String medicalProcedureId) {
            this.medicalProcedureId = medicalProcedureId;
        }

        public int getMedicalAppointmentStateId() {
            return medicalAppointmentStateId;
        }

        public void setMedicalAppointmentStateId(int medicalAppointmentStateId) {
            this.medicalAppointmentStateId = medicalAppointmentStateId;
        }

        public String getMedicalAppointmentDateTime() {
            return medicalAppointmentDateTime;
        }

        public void setMedicalAppointmentDateTime(String medicalAppointmentDateTime) {
            this.medicalAppointmentDateTime = medicalAppointmentDateTime;
        }

        public String getMedicalAppointmentCancellationReason() {
            return medicalAppointmentCancellationReason;
        }

        public void setMedicalAppointmentCancellationReason(String medicalAppointmentCancellationReason) {
            this.medicalAppointmentCancellationReason = medicalAppointmentCancellationReason;
        }

        public String getMedicalAppointmentNotes() {
            return medicalAppointmentNotes;
        }

        public void setMedicalAppointmentNotes(String medicalAppointmentNotes) {
            this.medicalAppointmentNotes = medicalAppointmentNotes;
        }

        public String getMedicalAppointmentCreatedAt() {
            return medicalAppointmentCreatedAt;
        }

        public void setMedicalAppointmentCreatedAt(String medicalAppointmentCreatedAt) {
            this.medicalAppointmentCreatedAt = medicalAppointmentCreatedAt;
        }

        public String getMedicalAppointmentUpdatedAt() {
            return medicalAppointmentUpdatedAt;
        }

        public void setMedicalAppointmentUpdatedAt(String medicalAppointmentUpdatedAt) {
            this.medicalAppointmentUpdatedAt = medicalAppointmentUpdatedAt;
        }

        @Override
        public String toString() {
            return "AppointmentData{" +
                    "id='" + medicalAppointmentId + '\'' +
                    ", patientId='" + patientUserId + '\'' +
                    ", branchId='" + branchId + '\'' +
                    ", procedureId='" + medicalProcedureId + '\'' +
                    ", state=" + medicalAppointmentStateId +
                    ", hasDoctor=" + hasDoctor() +
                    ", hasDateTime=" + hasDateTime() +
                    '}';
        }
    }
}