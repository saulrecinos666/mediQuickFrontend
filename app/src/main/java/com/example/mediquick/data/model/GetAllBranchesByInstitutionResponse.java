// GetAllBranchesByInstitutionResponse.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetAllBranchesByInstitutionResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<BranchData> data;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor vacío
    public GetAllBranchesByInstitutionResponse() {}

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

    public List<BranchData> getData() {
        return data;
    }

    public void setData(List<BranchData> data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Métodos utilitarios
    public int getTotalBranches() {
        return data != null ? data.size() : 0;
    }

    public boolean hasBranches() {
        return data != null && !data.isEmpty();
    }

    // Clase para los datos de cada sucursal
    public static class BranchData {

        @SerializedName("branch_id")
        private String branchId;

        @SerializedName("institution_id")
        private String institutionId;

        @SerializedName("institution")
        private InstitutionInfo institution;

        @SerializedName("branch_name")
        private String branchName;

        @SerializedName("branch_acronym")
        private String branchAcronym;

        @SerializedName("branch_description")
        private String branchDescription;

        @SerializedName("branch_longitude")
        private Double branchLongitude; // null en los ejemplos

        @SerializedName("branch_latitude")
        private Double branchLatitude; // null en los ejemplos

        @SerializedName("branch_full_address")
        private String branchFullAddress;

        @SerializedName("branch_status")
        private boolean branchStatus;

        // Constructor vacío
        public BranchData() {}

        // Métodos utilitarios
        public String getDisplayName() {
            if (branchAcronym != null && !branchAcronym.isEmpty()) {
                return branchAcronym + " - " + branchName;
            }
            return branchName;
        }

        public String getFullInfo() {
            StringBuilder info = new StringBuilder();
            info.append(getDisplayName());
            if (branchDescription != null && !branchDescription.isEmpty()) {
                info.append("\n").append(branchDescription);
            }
            if (branchFullAddress != null && !branchFullAddress.isEmpty()) {
                info.append("\n").append(branchFullAddress);
            }
            return info.toString();
        }

        public boolean isActive() {
            return branchStatus;
        }

        public boolean hasCoordinates() {
            return branchLongitude != null && branchLatitude != null;
        }

        public String getInstitutionDisplayName() {
            if (institution != null) {
                return institution.getDisplayName();
            }
            return "Institución no disponible";
        }

        // Método para usar en Spinner
        @Override
        public String toString() {
            return getDisplayName();
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

        public InstitutionInfo getInstitution() {
            return institution;
        }

        public void setInstitution(InstitutionInfo institution) {
            this.institution = institution;
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

        public String getBranchDescription() {
            return branchDescription;
        }

        public void setBranchDescription(String branchDescription) {
            this.branchDescription = branchDescription;
        }

        public Double getBranchLongitude() {
            return branchLongitude;
        }

        public void setBranchLongitude(Double branchLongitude) {
            this.branchLongitude = branchLongitude;
        }

        public Double getBranchLatitude() {
            return branchLatitude;
        }

        public void setBranchLatitude(Double branchLatitude) {
            this.branchLatitude = branchLatitude;
        }

        public String getBranchFullAddress() {
            return branchFullAddress;
        }

        public void setBranchFullAddress(String branchFullAddress) {
            this.branchFullAddress = branchFullAddress;
        }

        public boolean isBranchStatus() {
            return branchStatus;
        }

        public void setBranchStatus(boolean branchStatus) {
            this.branchStatus = branchStatus;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BranchData that = (BranchData) obj;
            return branchId != null ? branchId.equals(that.branchId) : that.branchId == null;
        }

        @Override
        public int hashCode() {
            return branchId != null ? branchId.hashCode() : 0;
        }
    }

    // Clase para la información de la institución dentro de la sucursal
    public static class InstitutionInfo {

        @SerializedName("institution_acronym")
        private String institutionAcronym;

        @SerializedName("institution_name")
        private String institutionName;

        // Constructor vacío
        public InstitutionInfo() {}

        // Método utilitario
        public String getDisplayName() {
            if (institutionAcronym != null && !institutionAcronym.isEmpty()) {
                return institutionAcronym + " - " + institutionName;
            }
            return institutionName != null ? institutionName : "Institución sin nombre";
        }

        // Getters y Setters
        public String getInstitutionAcronym() {
            return institutionAcronym;
        }

        public void setInstitutionAcronym(String institutionAcronym) {
            this.institutionAcronym = institutionAcronym;
        }

        public String getInstitutionName() {
            return institutionName;
        }

        public void setInstitutionName(String institutionName) {
            this.institutionName = institutionName;
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }
}