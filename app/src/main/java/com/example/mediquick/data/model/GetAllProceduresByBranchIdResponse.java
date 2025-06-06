// GetAllProceduresByBranchIdResponse.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetAllProceduresByBranchIdResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ProcedureData> data;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor vacío
    public GetAllProceduresByBranchIdResponse() {}

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

    public List<ProcedureData> getData() {
        return data;
    }

    public void setData(List<ProcedureData> data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Métodos utilitarios
    public int getTotalProcedures() {
        return data != null ? data.size() : 0;
    }

    public boolean hasProcedures() {
        return data != null && !data.isEmpty();
    }

    // Clase para los datos de cada procedimiento médico
    public static class ProcedureData {

        @SerializedName("medical_procedure_id")
        private String medicalProcedureId;

        @SerializedName("specialty_id")
        private int specialtyId;

        @SerializedName("specialty")
        private SpecialtyInfo specialty;

        @SerializedName("branch_id")
        private String branchId;

        @SerializedName("branch")
        private BranchInfo branch;

        @SerializedName("medical_procedure_name")
        private String medicalProcedureName;

        @SerializedName("medical_procedure_photo_url")
        private String medicalProcedurePhotoUrl;

        @SerializedName("medical_procedure_estimated_duration")
        private String medicalProcedureEstimatedDuration;

        @SerializedName("medical_procedure_requires_confirmation")
        private boolean medicalProcedureRequiresConfirmation;

        @SerializedName("medical_procedure_cost")
        private String medicalProcedureCost;

        @SerializedName("medical_procedure_available_online")
        private boolean medicalProcedureAvailableOnline;

        @SerializedName("medical_procedure_available_slots")
        private int medicalProcedureAvailableSlots;

        @SerializedName("medical_procedure_is_active")
        private boolean medicalProcedureIsActive;

        // Constructor vacío
        public ProcedureData() {}

        // Métodos utilitarios
        public boolean isActive() {
            return medicalProcedureIsActive;
        }

        public boolean isAvailableOnline() {
            return medicalProcedureAvailableOnline;
        }

        public boolean requiresConfirmation() {
            return medicalProcedureRequiresConfirmation;
        }

        public boolean hasAvailableSlots() {
            return medicalProcedureAvailableSlots > 0;
        }

        public boolean isFree() {
            return "0".equals(medicalProcedureCost) || "0.0".equals(medicalProcedureCost);
        }

        public String getFormattedCost() {
            if (isFree()) {
                return "Gratuito";
            }
            return "$" + medicalProcedureCost;
        }

        public String getFormattedDuration() {
            return medicalProcedureEstimatedDuration + " minutos";
        }

        public String getSpecialtyName() {
            if (specialty != null) {
                return specialty.getSpecialtyName();
            }
            return "Especialidad no disponible";
        }

        public String getBranchName() {
            if (branch != null) {
                return branch.getBranchName();
            }
            return "Sucursal no disponible";
        }

        public String getProcedureInfo() {
            StringBuilder info = new StringBuilder();
            info.append(medicalProcedureName);
            info.append(" • ").append(getFormattedCost());
            info.append(" • ").append(getFormattedDuration());
            if (isAvailableOnline()) {
                info.append(" • Online");
            }
            return info.toString();
        }

        // Método para usar en Spinner
        @Override
        public String toString() {
            return medicalProcedureName;
        }

        // Getters y Setters
        public String getMedicalProcedureId() {
            return medicalProcedureId;
        }

        public void setMedicalProcedureId(String medicalProcedureId) {
            this.medicalProcedureId = medicalProcedureId;
        }

        public int getSpecialtyId() {
            return specialtyId;
        }

        public void setSpecialtyId(int specialtyId) {
            this.specialtyId = specialtyId;
        }

        public SpecialtyInfo getSpecialty() {
            return specialty;
        }

        public void setSpecialty(SpecialtyInfo specialty) {
            this.specialty = specialty;
        }

        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }

        public BranchInfo getBranch() {
            return branch;
        }

        public void setBranch(BranchInfo branch) {
            this.branch = branch;
        }

        public String getMedicalProcedureName() {
            return medicalProcedureName;
        }

        public void setMedicalProcedureName(String medicalProcedureName) {
            this.medicalProcedureName = medicalProcedureName;
        }

        public String getMedicalProcedurePhotoUrl() {
            return medicalProcedurePhotoUrl;
        }

        public void setMedicalProcedurePhotoUrl(String medicalProcedurePhotoUrl) {
            this.medicalProcedurePhotoUrl = medicalProcedurePhotoUrl;
        }

        public String getMedicalProcedureEstimatedDuration() {
            return medicalProcedureEstimatedDuration;
        }

        public void setMedicalProcedureEstimatedDuration(String medicalProcedureEstimatedDuration) {
            this.medicalProcedureEstimatedDuration = medicalProcedureEstimatedDuration;
        }

        public boolean isMedicalProcedureRequiresConfirmation() {
            return medicalProcedureRequiresConfirmation;
        }

        public void setMedicalProcedureRequiresConfirmation(boolean medicalProcedureRequiresConfirmation) {
            this.medicalProcedureRequiresConfirmation = medicalProcedureRequiresConfirmation;
        }

        public String getMedicalProcedureCost() {
            return medicalProcedureCost;
        }

        public void setMedicalProcedureCost(String medicalProcedureCost) {
            this.medicalProcedureCost = medicalProcedureCost;
        }

        public boolean isMedicalProcedureAvailableOnline() {
            return medicalProcedureAvailableOnline;
        }

        public void setMedicalProcedureAvailableOnline(boolean medicalProcedureAvailableOnline) {
            this.medicalProcedureAvailableOnline = medicalProcedureAvailableOnline;
        }

        public int getMedicalProcedureAvailableSlots() {
            return medicalProcedureAvailableSlots;
        }

        public void setMedicalProcedureAvailableSlots(int medicalProcedureAvailableSlots) {
            this.medicalProcedureAvailableSlots = medicalProcedureAvailableSlots;
        }

        public boolean isMedicalProcedureIsActive() {
            return medicalProcedureIsActive;
        }

        public void setMedicalProcedureIsActive(boolean medicalProcedureIsActive) {
            this.medicalProcedureIsActive = medicalProcedureIsActive;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ProcedureData that = (ProcedureData) obj;
            return medicalProcedureId != null ? medicalProcedureId.equals(that.medicalProcedureId) : that.medicalProcedureId == null;
        }

        @Override
        public int hashCode() {
            return medicalProcedureId != null ? medicalProcedureId.hashCode() : 0;
        }
    }

    // Clase para información de la especialidad
    public static class SpecialtyInfo {

        @SerializedName("specialty_id")
        private int specialtyId;

        @SerializedName("specialty_name")
        private String specialtyName;

        @SerializedName("specialt_description") // Nota: typo en el JSON original
        private String specialtyDescription;

        // Constructor vacío
        public SpecialtyInfo() {}

        // Getters y Setters
        public int getSpecialtyId() {
            return specialtyId;
        }

        public void setSpecialtyId(int specialtyId) {
            this.specialtyId = specialtyId;
        }

        public String getSpecialtyName() {
            return specialtyName;
        }

        public void setSpecialtyName(String specialtyName) {
            this.specialtyName = specialtyName;
        }

        public String getSpecialtyDescription() {
            return specialtyDescription;
        }

        public void setSpecialtyDescription(String specialtyDescription) {
            this.specialtyDescription = specialtyDescription;
        }

        @Override
        public String toString() {
            return specialtyName;
        }
    }

    // Clase para información básica de la sucursal
    public static class BranchInfo {

        @SerializedName("branch_id")
        private String branchId;

        @SerializedName("institution_id")
        private String institutionId;

        @SerializedName("branch_name")
        private String branchName;

        @SerializedName("branch_acronym")
        private String branchAcronym;

        @SerializedName("branch_status")
        private boolean branchStatus;

        // Constructor vacío
        public BranchInfo() {}

        // Método utilitario
        public String getDisplayName() {
            if (branchAcronym != null && !branchAcronym.isEmpty()) {
                return branchAcronym + " - " + branchName;
            }
            return branchName;
        }

        public boolean isActive() {
            return branchStatus;
        }

        // Getters y Setters
        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }

        public String getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(String institutionId) {
            this.institutionId = institutionId;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getBranchAcronym() {
            return branchAcronym;
        }

        public void setBranchAcronym(String branchAcronym) {
            this.branchAcronym = branchAcronym;
        }

        public boolean isBranchStatus() {
            return branchStatus;
        }

        public void setBranchStatus(boolean branchStatus) {
            this.branchStatus = branchStatus;
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }
}