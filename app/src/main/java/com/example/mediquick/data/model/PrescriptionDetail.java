package com.example.mediquick.data.model;
import java.util.List;

public class PrescriptionDetail {
    private String appointmentId;
    private String procedure;
    private String dateTime;
    private String doctor;
    private String branch;
    private String prescriptionNotes;
    private List<PrescriptionItem> items;

    public PrescriptionDetail(String appointmentId, String procedure, String dateTime, String doctor, String branch, String prescriptionNotes, List<PrescriptionItem> items) {
        this.appointmentId = appointmentId;
        this.procedure = procedure;
        this.dateTime = dateTime;
        this.doctor = doctor;
        this.branch = branch;
        this.prescriptionNotes = prescriptionNotes;
        this.items = items;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getProcedure() { return procedure; }
    public String getDateTime() { return dateTime; }
    public String getDoctor() { return doctor; }
    public String getBranch() { return branch; }
    public String getPrescriptionNotes() { return prescriptionNotes; }
    public List<PrescriptionItem> getItems() { return items; }
}
