// PrescriptionRequest.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionRequest {

    @SerializedName("medicalAppointmentId")
    private String medicalAppointmentId;

    @SerializedName("prescriptionNotes")
    private String prescriptionNotes;

    @SerializedName("prescriptionItem")
    private List<PrescriptionItem> prescriptionItem;

    // Constructor vacío
    public PrescriptionRequest() {}

    // Constructor con parámetros
    public PrescriptionRequest(String medicalAppointmentId, String prescriptionNotes, List<PrescriptionItem> prescriptionItem) {
        this.medicalAppointmentId = medicalAppointmentId;
        this.prescriptionNotes = prescriptionNotes;
        this.prescriptionItem = prescriptionItem;
    }

    // Getters y Setters
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

    public List<PrescriptionItem> getPrescriptionItem() {
        return prescriptionItem;
    }

    public void setPrescriptionItem(List<PrescriptionItem> prescriptionItem) {
        this.prescriptionItem = prescriptionItem;
    }

    // Clase interna para los elementos de la prescripción
    public static class PrescriptionItem {

        @SerializedName("administrationRouteId")
        private int administrationRouteId;

        @SerializedName("prescriptionItemMedicationName")
        private String prescriptionItemMedicationName;

        @SerializedName("prescriptionItemDosage")
        private String prescriptionItemDosage;

        @SerializedName("prescriptionItemFrequency")
        private String prescriptionItemFrequency;

        @SerializedName("prescriptionItemDuration")
        private String prescriptionItemDuration;

        @SerializedName("prescriptionItemUnit")
        private String prescriptionItemUnit;

        @SerializedName("prescriptionItemItemNotes")
        private String prescriptionItemItemNotes;

        // Constructor vacío
        public PrescriptionItem() {}

        // Constructor con parámetros
        public PrescriptionItem(int administrationRouteId, String prescriptionItemMedicationName,
                                String prescriptionItemDosage, String prescriptionItemFrequency,
                                String prescriptionItemDuration, String prescriptionItemUnit,
                                String prescriptionItemItemNotes) {
            this.administrationRouteId = administrationRouteId;
            this.prescriptionItemMedicationName = prescriptionItemMedicationName;
            this.prescriptionItemDosage = prescriptionItemDosage;
            this.prescriptionItemFrequency = prescriptionItemFrequency;
            this.prescriptionItemDuration = prescriptionItemDuration;
            this.prescriptionItemUnit = prescriptionItemUnit;
            this.prescriptionItemItemNotes = prescriptionItemItemNotes;
        }

        // Constructor desde PrescriptionForm (para convertir datos del formulario)
        public PrescriptionItem(PrescriptionForm form, int administrationRouteId) {
            this.administrationRouteId = administrationRouteId;
            this.prescriptionItemMedicationName = form.getNombre();
            this.prescriptionItemDosage = form.getDosis();
            this.prescriptionItemFrequency = form.getFrecuencia();
            this.prescriptionItemDuration = form.getDuracion();
            this.prescriptionItemUnit = "mg"; // Valor por defecto, puedes cambiarlo
            this.prescriptionItemItemNotes = form.getNotas() != null ? form.getNotas() : "";
        }

        // Getters y Setters
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
    }

    // Método utilitario para crear request desde datos del formulario
    public static PrescriptionRequest fromFormData(String appointmentId, String notes, List<PrescriptionForm> medicamentos) {
        PrescriptionRequest request = new PrescriptionRequest();
        request.setMedicalAppointmentId(appointmentId);
        request.setPrescriptionNotes(notes);

        List<PrescriptionItem> items = new ArrayList<>();
        for (PrescriptionForm form : medicamentos) {
            // Usar administrationRouteId = 1 por defecto (puedes ajustar según tus necesidades)
            items.add(new PrescriptionItem(form, 1));
        }
        request.setPrescriptionItem(items);

        return request;
    }

    // Método utilitario para validación
    public boolean isValid() {
        if (medicalAppointmentId == null || medicalAppointmentId.trim().isEmpty()) {
            return false;
        }

        if (prescriptionItem == null || prescriptionItem.isEmpty()) {
            return false;
        }

        for (PrescriptionItem item : prescriptionItem) {
            if (item.getPrescriptionItemMedicationName() == null || item.getPrescriptionItemMedicationName().trim().isEmpty() ||
                    item.getPrescriptionItemDosage() == null || item.getPrescriptionItemDosage().trim().isEmpty() ||
                    item.getPrescriptionItemFrequency() == null || item.getPrescriptionItemFrequency().trim().isEmpty() ||
                    item.getPrescriptionItemDuration() == null || item.getPrescriptionItemDuration().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "PrescriptionRequest{" +
                "medicalAppointmentId='" + medicalAppointmentId + '\'' +
                ", prescriptionNotes='" + prescriptionNotes + '\'' +
                ", prescriptionItem=" + prescriptionItem +
                '}';
    }
}