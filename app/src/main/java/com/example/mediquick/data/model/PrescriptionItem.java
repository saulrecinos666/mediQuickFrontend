package com.example.mediquick.data.model;

public class PrescriptionItem {
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String notes;

    public PrescriptionItem(String medicationName, String dosage, String frequency, String duration, String notes) {
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
    }

    public String getMedicationName() { return medicationName; }
    public String getDosage() { return dosage; }
    public String getFrequency() { return frequency; }
    public String getDuration() { return duration; }
    public String getNotes() { return notes; }
}
