package com.example.mediquick.data.model;

public class AppointmentSummary {
    private String appointmentId;
    private String dateTime;
    private String stateDescription;
    private String doctorName;
    private String branchName;

    // Constructor
    public AppointmentSummary(String appointmentId, String dateTime, String stateDescription, String doctorName, String branchName) {
        this.appointmentId = appointmentId;
        this.dateTime = dateTime;
        this.stateDescription = stateDescription;
        this.doctorName = doctorName;
        this.branchName = branchName;
    }

    // Getters
    public String getAppointmentId() {
        return appointmentId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getBranchName() {
        return branchName;
    }

    // Setters
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setStateDescription(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public String toString() {
        return "AppointmentSummary{" +
                "appointmentId='" + appointmentId + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", stateDescription='" + stateDescription + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", branchName='" + branchName + '\'' +
                '}';
    }
}