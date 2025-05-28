package com.example.mediquick.data.model;

public class Appointment {
    private String medical_appointment_id;
    private String non_registered_patient_id;
    private String patient_user_id;
    private String doctor_user_id;
    private String appointment_scheduler_id;
    private String branch_id;
    private String medical_procedure_id;
    private int medical_appointment_state_id;
    private String medical_appointment_date_time;
    private String medical_appointment_cancellation_reason;
    private String medical_appointment_notes;
    private String medical_appointment_created_at;
    private String medical_appointment_updated_at;

    // Getters y setters
    public String getMedical_appointment_id() { return medical_appointment_id; }
    public void setMedical_appointment_id(String id) { this.medical_appointment_id = id; }

    public String getNon_registered_patient_id() { return non_registered_patient_id; }
    public void setNon_registered_patient_id(String id) { this.non_registered_patient_id = id; }

    public String getPatient_user_id() { return patient_user_id; }
    public void setPatient_user_id(String id) { this.patient_user_id = id; }

    public String getDoctor_user_id() { return doctor_user_id; }
    public void setDoctor_user_id(String id) { this.doctor_user_id = id; }

    public String getAppointment_scheduler_id() { return appointment_scheduler_id; }
    public void setAppointment_scheduler_id(String id) { this.appointment_scheduler_id = id; }

    public String getBranch_id() { return branch_id; }
    public void setBranch_id(String id) { this.branch_id = id; }

    public String getMedical_procedure_id() { return medical_procedure_id; }
    public void setMedical_procedure_id(String id) { this.medical_procedure_id = id; }

    public int getMedical_appointment_state_id() { return medical_appointment_state_id; }
    public void setMedical_appointment_state_id(int id) { this.medical_appointment_state_id = id; }

    public String getMedical_appointment_date_time() { return medical_appointment_date_time; }
    public void setMedical_appointment_date_time(String dateTime) { this.medical_appointment_date_time = dateTime; }

    public String getMedical_appointment_cancellation_reason() { return medical_appointment_cancellation_reason; }
    public void setMedical_appointment_cancellation_reason(String reason) { this.medical_appointment_cancellation_reason = reason; }

    public String getMedical_appointment_notes() { return medical_appointment_notes; }
    public void setMedical_appointment_notes(String notes) { this.medical_appointment_notes = notes; }

    public String getMedical_appointment_created_at() { return medical_appointment_created_at; }
    public void setMedical_appointment_created_at(String createdAt) { this.medical_appointment_created_at = createdAt; }

    public String getMedical_appointment_updated_at() { return medical_appointment_updated_at; }
    public void setMedical_appointment_updated_at(String updatedAt) { this.medical_appointment_updated_at = updatedAt; }
}
