package com.example.mediquick.data.model;

public class Institution {
    private String institutionId;
    private String institutionName;
    private String institutionDescription;
    private boolean institutionStatus;

    // Getters y Setters
    public String getInstitutionId() { return institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getInstitutionDescription() { return institutionDescription; }
    public void setInstitutionDescription(String institutionDescription) { this.institutionDescription = institutionDescription; }

    public boolean isInstitutionStatus() { return institutionStatus; }
    public void setInstitutionStatus(boolean institutionStatus) { this.institutionStatus = institutionStatus; }
}
