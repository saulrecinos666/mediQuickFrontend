// ResponsePrescription.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponsePrescription {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private PrescriptionData data;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor vacío
    public ResponsePrescription() {}

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

    public PrescriptionData getData() {
        return data;
    }

    public void setData(PrescriptionData data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Clase para los datos de la prescripción creada
    public static class PrescriptionData {

        @SerializedName("prescription_id")
        private int prescriptionId;

        @SerializedName("medical_appointment_id")
        private String medicalAppointmentId;

        @SerializedName("prescription_notes")
        private String prescriptionNotes;

        @SerializedName("prescription_fecha_emision")
        private String prescriptionFechaEmision;

        @SerializedName("prescription_created_at")
        private String prescriptionCreatedAt;

        @SerializedName("prescription_updated_at")
        private String prescriptionUpdatedAt;

        @SerializedName("prescription_item")
        private List<PrescriptionItemData> prescriptionItem;

        // Constructor vacío
        public PrescriptionData() {}

        // Métodos utilitarios
        public int getTotalMedicamentos() {
            return prescriptionItem != null ? prescriptionItem.size() : 0;
        }

        public String getFormattedEmissionDate() {
            // Puedes agregar lógica para formatear la fecha aquí
            return prescriptionFechaEmision;
        }

        public boolean hasItems() {
            return prescriptionItem != null && !prescriptionItem.isEmpty();
        }

        // Getters y Setters
        public int getPrescriptionId() {
            return prescriptionId;
        }

        public void setPrescriptionId(int prescriptionId) {
            this.prescriptionId = prescriptionId;
        }

        public String getMedicalAppointmentId() {
            return medicalAppointmentId;
        }

        public void setMedicalAppointmentId(String medicalAppointmentId) {
            this.medicalAppointmentId = medicalAppointmentId;
        }

        public String getPrescriptionNotes() {
            return prescriptionNotes;
        }

        public void setPrescriptionNotes(String prescriptionNotes) {
            this.prescriptionNotes = prescriptionNotes;
        }

        public String getPrescriptionFechaEmision() {
            return prescriptionFechaEmision;
        }

        public void setPrescriptionFechaEmision(String prescriptionFechaEmision) {
            this.prescriptionFechaEmision = prescriptionFechaEmision;
        }

        public String getPrescriptionCreatedAt() {
            return prescriptionCreatedAt;
        }

        public void setPrescriptionCreatedAt(String prescriptionCreatedAt) {
            this.prescriptionCreatedAt = prescriptionCreatedAt;
        }

        public String getPrescriptionUpdatedAt() {
            return prescriptionUpdatedAt;
        }

        public void setPrescriptionUpdatedAt(String prescriptionUpdatedAt) {
            this.prescriptionUpdatedAt = prescriptionUpdatedAt;
        }

        public List<PrescriptionItemData> getPrescriptionItem() {
            return prescriptionItem;
        }

        public void setPrescriptionItem(List<PrescriptionItemData> prescriptionItem) {
            this.prescriptionItem = prescriptionItem;
        }
    }

    // Clase para los elementos de la prescripción en la respuesta
    public static class PrescriptionItemData {

        @SerializedName("prescription_item_id")
        private int prescriptionItemId;

        @SerializedName("prescription_id")
        private int prescriptionId;

        @SerializedName("administration_route_id")
        private int administrationRouteId;

        @SerializedName("prescription_item_medication_name")
        private String prescriptionItemMedicationName;

        @SerializedName("prescription_item_dosage")
        private String prescriptionItemDosage;

        @SerializedName("prescription_item_frequency")
        private String prescriptionItemFrequency;

        @SerializedName("prescription_item_duration")
        private String prescriptionItemDuration;

        @SerializedName("prescription_item_unit")
        private String prescriptionItemUnit;

        @SerializedName("prescription_item_item_notes")
        private String prescriptionItemItemNotes;

        @SerializedName("prescription_item_created_at")
        private String prescriptionItemCreatedAt;

        @SerializedName("prescription_item_updated_at")
        private String prescriptionItemUpdatedAt;

        // Constructor vacío
        public PrescriptionItemData() {}

        // Método utilitario para obtener información resumida del medicamento
        public String getMedicationSummary() {
            StringBuilder summary = new StringBuilder();
            if (prescriptionItemMedicationName != null) {
                summary.append(prescriptionItemMedicationName);
            }
            if (prescriptionItemDosage != null) {
                summary.append(" - ").append(prescriptionItemDosage);
            }
            if (prescriptionItemFrequency != null) {
                summary.append(" (").append(prescriptionItemFrequency).append(")");
            }
            return summary.toString();
        }

        // Getters y Setters
        public int getPrescriptionItemId() {
            return prescriptionItemId;
        }

        public void setPrescriptionItemId(int prescriptionItemId) {
            this.prescriptionItemId = prescriptionItemId;
        }

        public int getPrescriptionId() {
            return prescriptionId;
        }

        public void setPrescriptionId(int prescriptionId) {
            this.prescriptionId = prescriptionId;
        }

        public int getAdministrationRouteId() {
            return administrationRouteId;
        }

        public void setAdministrationRouteId(int administrationRouteId) {
            this.administrationRouteId = administrationRouteId;
        }

        public String getPrescriptionItemMedicationName() {
            return prescriptionItemMedicationName;
        }

        public void setPrescriptionItemMedicationName(String prescriptionItemMedicationName) {
            this.prescriptionItemMedicationName = prescriptionItemMedicationName;
        }

        public String getPrescriptionItemDosage() {
            return prescriptionItemDosage;
        }

        public void setPrescriptionItemDosage(String prescriptionItemDosage) {
            this.prescriptionItemDosage = prescriptionItemDosage;
        }

        public String getPrescriptionItemFrequency() {
            return prescriptionItemFrequency;
        }

        public void setPrescriptionItemFrequency(String prescriptionItemFrequency) {
            this.prescriptionItemFrequency = prescriptionItemFrequency;
        }

        public String getPrescriptionItemDuration() {
            return prescriptionItemDuration;
        }

        public void setPrescriptionItemDuration(String prescriptionItemDuration) {
            this.prescriptionItemDuration = prescriptionItemDuration;
        }

        public String getPrescriptionItemUnit() {
            return prescriptionItemUnit;
        }

        public void setPrescriptionItemUnit(String prescriptionItemUnit) {
            this.prescriptionItemUnit = prescriptionItemUnit;
        }

        public String getPrescriptionItemItemNotes() {
            return prescriptionItemItemNotes;
        }

        public void setPrescriptionItemItemNotes(String prescriptionItemItemNotes) {
            this.prescriptionItemItemNotes = prescriptionItemItemNotes;
        }

        public String getPrescriptionItemCreatedAt() {
            return prescriptionItemCreatedAt;
        }

        public void setPrescriptionItemCreatedAt(String prescriptionItemCreatedAt) {
            this.prescriptionItemCreatedAt = prescriptionItemCreatedAt;
        }

        public String getPrescriptionItemUpdatedAt() {
            return prescriptionItemUpdatedAt;
        }

        public void setPrescriptionItemUpdatedAt(String prescriptionItemUpdatedAt) {
            this.prescriptionItemUpdatedAt = prescriptionItemUpdatedAt;
        }

        @Override
        public String toString() {
            return "PrescriptionItemData{" +
                    "id=" + prescriptionItemId +
                    ", medication='" + prescriptionItemMedicationName + '\'' +
                    ", dosage='" + prescriptionItemDosage + '\'' +
                    ", frequency='" + prescriptionItemFrequency + '\'' +
                    ", duration='" + prescriptionItemDuration + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ResponsePrescription{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", prescriptionId=" + (data != null ? data.getPrescriptionId() : "null") +
                ", totalItems=" + (data != null ? data.getTotalMedicamentos() : 0) +
                '}';
    }
}