package com.example.mediquick.data.model;

public class AppointmentSummary {
    private String appointmentId;
    private String dateTime;
    private String stateDescription;
    private String doctorName;
    private String branchName;

    public AppointmentSummary(String appointmentId, String dateTime, String stateDescription, String doctorName, String branchName) {
        this.appointmentId = appointmentId;
        this.dateTime = dateTime;
        this.stateDescription = stateDescription;
        this.doctorName = doctorName;
        this.branchName = branchName;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getDateTime() { return dateTime; }
    public String getStateDescription() { return stateDescription; }
    public String getDoctorName() { return doctorName; }
    public String getBranchName() { return branchName; }
}
