package com.example.mediquick.data.model;

public class MedicalProcedure {
    private String procedureId;
    private String procedureName;
    private String procedureDuration;
    private double procedureCost;
    private boolean availableOnline;
    private String specialty;

    // Getters y Setters
    public String getProcedureId() { return procedureId; }
    public void setProcedureId(String procedureId) { this.procedureId = procedureId; }

    public String getProcedureName() { return procedureName; }
    public void setProcedureName(String procedureName) { this.procedureName = procedureName; }

    public String getProcedureDuration() { return procedureDuration; }
    public void setProcedureDuration(String procedureDuration) { this.procedureDuration = procedureDuration; }

    public double getProcedureCost() { return procedureCost; }
    public void setProcedureCost(double procedureCost) { this.procedureCost = procedureCost; }

    public boolean isAvailableOnline() { return availableOnline; }
    public void setAvailableOnline(boolean availableOnline) { this.availableOnline = availableOnline; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
