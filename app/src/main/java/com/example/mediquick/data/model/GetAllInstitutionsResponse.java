// GetAllInstitutionsResponse.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetAllInstitutionsResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<InstitutionData> data;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor vacío
    public GetAllInstitutionsResponse() {}

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

    public List<InstitutionData> getData() {
        return data;
    }

    public void setData(List<InstitutionData> data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Métodos utilitarios
    public int getTotalInstitutions() {
        return data != null ? data.size() : 0;
    }

    public boolean hasInstitutions() {
        return data != null && !data.isEmpty();
    }

    // Clase para los datos de cada institución
    public static class InstitutionData {

        @SerializedName("institution_id")
        private String institutionId;

        @SerializedName("institution_name")
        private String institutionName;

        @SerializedName("institution_logo")
        private String institutionLogo; // null en los ejemplos

        @SerializedName("institution_acronym")
        private String institutionAcronym;

        @SerializedName("institution_description")
        private String institutionDescription;

        @SerializedName("institution_status")
        private boolean institutionStatus;

        @SerializedName("institution_created_at")
        private String institutionCreatedAt;

        @SerializedName("institution_updated_at")
        private String institutionUpdatedAt;

        // Constructor vacío
        public InstitutionData() {}

        // Métodos utilitarios
        public String getDisplayName() {
            if (institutionAcronym != null && !institutionAcronym.isEmpty()) {
                return institutionAcronym + " - " + institutionName;
            }
            return institutionName;
        }

        public boolean isActive() {
            return institutionStatus;
        }

        public boolean hasLogo() {
            return institutionLogo != null && !institutionLogo.isEmpty();
        }

        // Método para usar en Spinner
        @Override
        public String toString() {
            return getDisplayName();
        }

        // Getters y Setters
        public String getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(String institutionId) {
            this.institutionId = institutionId;
        }

        public String getInstitutionName() {
            return institutionName;
        }

        public void setInstitutionName(String institutionName) {
            this.institutionName = institutionName;
        }

        public String getInstitutionLogo() {
            return institutionLogo;
        }

        public void setInstitutionLogo(String institutionLogo) {
            this.institutionLogo = institutionLogo;
        }

        public String getInstitutionAcronym() {
            return institutionAcronym;
        }

        public void setInstitutionAcronym(String institutionAcronym) {
            this.institutionAcronym = institutionAcronym;
        }

        public String getInstitutionDescription() {
            return institutionDescription;
        }

        public void setInstitutionDescription(String institutionDescription) {
            this.institutionDescription = institutionDescription;
        }

        public boolean isInstitutionStatus() {
            return institutionStatus;
        }

        public void setInstitutionStatus(boolean institutionStatus) {
            this.institutionStatus = institutionStatus;
        }

        public String getInstitutionCreatedAt() {
            return institutionCreatedAt;
        }

        public void setInstitutionCreatedAt(String institutionCreatedAt) {
            this.institutionCreatedAt = institutionCreatedAt;
        }

        public String getInstitutionUpdatedAt() {
            return institutionUpdatedAt;
        }

        public void setInstitutionUpdatedAt(String institutionUpdatedAt) {
            this.institutionUpdatedAt = institutionUpdatedAt;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            InstitutionData that = (InstitutionData) obj;
            return institutionId != null ? institutionId.equals(that.institutionId) : that.institutionId == null;
        }

        @Override
        public int hashCode() {
            return institutionId != null ? institutionId.hashCode() : 0;
        }
    }
}