// GetAppointmentResponse.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetAppointmentResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private AppointmentDetailData data;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor vacío
    public GetAppointmentResponse() {}

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

    public AppointmentDetailData getData() {
        return data;
    }

    public void setData(AppointmentDetailData data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Clase para el detalle completo de la cita
    public static class AppointmentDetailData {

        @SerializedName("medical_appointment_date_time")
        private String medicalAppointmentDateTime;

        @SerializedName("medical_appointment_cancellation_reason")
        private String medicalAppointmentCancellationReason;

        @SerializedName("medical_appointment_notes")
        private String medicalAppointmentNotes;

        @SerializedName("medical_appointment_created_at")
        private String medicalAppointmentCreatedAt;

        @SerializedName("medical_procedure")
        private MedicalProcedure medicalProcedure;

        @SerializedName("branch")
        private BranchDetail branch;

        @SerializedName("non_registered_patient")
        private Object nonRegisteredPatient; // null en el ejemplo

        @SerializedName("medical_exam")
        private List<Object> medicalExam; // Lista vacía, cambiar Object por el tipo correcto

        @SerializedName("doctor_user")
        private UserDetail doctorUser;

        @SerializedName("patient_user")
        private UserDetail patientUser;

        @SerializedName("medical_appointment_state")
        private AppointmentStateDetail medicalAppointmentState;

        @SerializedName("prescription")
        private List<PrescriptionDetail> prescription; // CORREGIDO: Tipo específico

        // Constructor vacío
        public AppointmentDetailData() {}

        // Métodos utilitarios
        public String getDoctorFullName() {
            if (doctorUser != null) {
                return doctorUser.getFullName();
            }
            return "Doctor no disponible";
        }

        public String getPatientFullName() {
            if (patientUser != null) {
                return patientUser.getFullName();
            }
            return "Paciente no disponible";
        }

        public String getFormattedDateTime() {
            // Puedes agregar lógica para formatear la fecha aquí
            return medicalAppointmentDateTime;
        }

        public String getProcedureName() {
            if (medicalProcedure != null) {
                return medicalProcedure.getMedicalProcedureName();
            }
            return "Procedimiento no especificado";
        }

        public String getBranchName() {
            if (branch != null) {
                return branch.getBranchName();
            }
            return "Sucursal no especificada";
        }

        // Métodos utilitarios para prescripciones
        public boolean hasPrescriptions() {
            return prescription != null && !prescription.isEmpty();
        }

        public int getTotalPrescriptions() {
            return prescription != null ? prescription.size() : 0;
        }

        // Getters y Setters
        public String getMedicalAppointmentDateTime() {
            return medicalAppointmentDateTime;
        }

        public void setMedicalAppointmentDateTime(String medicalAppointmentDateTime) {
            this.medicalAppointmentDateTime = medicalAppointmentDateTime;
        }

        public String getMedicalAppointmentCancellationReason() {
            return medicalAppointmentCancellationReason;
        }

        public void setMedicalAppointmentCancellationReason(String medicalAppointmentCancellationReason) {
            this.medicalAppointmentCancellationReason = medicalAppointmentCancellationReason;
        }

        public String getMedicalAppointmentNotes() {
            return medicalAppointmentNotes;
        }

        public void setMedicalAppointmentNotes(String medicalAppointmentNotes) {
            this.medicalAppointmentNotes = medicalAppointmentNotes;
        }

        public String getMedicalAppointmentCreatedAt() {
            return medicalAppointmentCreatedAt;
        }

        public void setMedicalAppointmentCreatedAt(String medicalAppointmentCreatedAt) {
            this.medicalAppointmentCreatedAt = medicalAppointmentCreatedAt;
        }

        public MedicalProcedure getMedicalProcedure() {
            return medicalProcedure;
        }

        public void setMedicalProcedure(MedicalProcedure medicalProcedure) {
            this.medicalProcedure = medicalProcedure;
        }

        public BranchDetail getBranch() {
            return branch;
        }

        public void setBranch(BranchDetail branch) {
            this.branch = branch;
        }

        public Object getNonRegisteredPatient() {
            return nonRegisteredPatient;
        }

        public void setNonRegisteredPatient(Object nonRegisteredPatient) {
            this.nonRegisteredPatient = nonRegisteredPatient;
        }

        public List<Object> getMedicalExam() {
            return medicalExam;
        }

        public void setMedicalExam(List<Object> medicalExam) {
            this.medicalExam = medicalExam;
        }

        public UserDetail getDoctorUser() {
            return doctorUser;
        }

        public void setDoctorUser(UserDetail doctorUser) {
            this.doctorUser = doctorUser;
        }

        public UserDetail getPatientUser() {
            return patientUser;
        }

        public void setPatientUser(UserDetail patientUser) {
            this.patientUser = patientUser;
        }

        public AppointmentStateDetail getMedicalAppointmentState() {
            return medicalAppointmentState;
        }

        public void setMedicalAppointmentState(AppointmentStateDetail medicalAppointmentState) {
            this.medicalAppointmentState = medicalAppointmentState;
        }

        public List<PrescriptionDetail> getPrescription() {
            return prescription;
        }

        public void setPrescription(List<PrescriptionDetail> prescription) {
            this.prescription = prescription;
        }
    }

    // Clase para el procedimiento médico
    public static class MedicalProcedure {

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

        @SerializedName("specialty")
        private Specialty specialty;

        // Constructor vacío
        public MedicalProcedure() {}

        // Getters y Setters
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

        public Specialty getSpecialty() {
            return specialty;
        }

        public void setSpecialty(Specialty specialty) {
            this.specialty = specialty;
        }
    }

    // Clase para la especialidad
    public static class Specialty {

        @SerializedName("specialty_name")
        private String specialtyName;

        @SerializedName("specialt_description") // Nota: typo en el JSON original
        private String specialtyDescription;

        // Constructor vacío
        public Specialty() {}

        // Getters y Setters
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
    }

    // Clase para detalles de la sucursal
    public static class BranchDetail {

        @SerializedName("branch_name")
        private String branchName;

        @SerializedName("branch_acronym")
        private String branchAcronym;

        @SerializedName("branch_description")
        private String branchDescription;

        @SerializedName("branch_full_address")
        private String branchFullAddress;

        @SerializedName("institution")
        private InstitutionDetail institution;

        // Constructor vacío
        public BranchDetail() {}

        // Getters y Setters
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

        public String getBranchFullAddress() {
            return branchFullAddress;
        }

        public void setBranchFullAddress(String branchFullAddress) {
            this.branchFullAddress = branchFullAddress;
        }

        public InstitutionDetail getInstitution() {
            return institution;
        }

        public void setInstitution(InstitutionDetail institution) {
            this.institution = institution;
        }
    }

    // Clase para detalles de la institución
    public static class InstitutionDetail {

        @SerializedName("institution_name")
        private String institutionName;

        @SerializedName("institution_acronym")
        private String institutionAcronym;

        @SerializedName("institution_description")
        private String institutionDescription;

        // Constructor vacío
        public InstitutionDetail() {}

        // Getters y Setters
        public String getInstitutionName() {
            return institutionName;
        }

        public void setInstitutionName(String institutionName) {
            this.institutionName = institutionName;
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
    }

    // Clase para detalles del usuario (doctor/paciente)
    public static class UserDetail {

        @SerializedName("user_state_id")
        private int userStateId;

        @SerializedName("user_gender")
        private String userGender;

        @SerializedName("user_first_name")
        private String userFirstName;

        @SerializedName("user_second_name")
        private String userSecondName;

        @SerializedName("user_third_name")
        private String userThirdName;

        @SerializedName("user_first_lastname")
        private String userFirstLastname;

        @SerializedName("user_second_lastname")
        private String userSecondLastname;

        @SerializedName("user_third_lastname")
        private String userThirdLastname;

        @SerializedName("user_email")
        private String userEmail;

        @SerializedName("user_phone_number")
        private String userPhoneNumber;

        // Constructor vacío
        public UserDetail() {}

        // Método utilitario para obtener nombre completo
        public String getFullName() {
            StringBuilder fullName = new StringBuilder();

            if (userFirstName != null && !userFirstName.isEmpty()) {
                fullName.append(userFirstName);
            }
            if (userSecondName != null && !userSecondName.isEmpty() && !userSecondName.trim().isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userSecondName);
            }
            if (userThirdName != null && !userThirdName.isEmpty() && !userThirdName.trim().isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userThirdName);
            }
            if (userFirstLastname != null && !userFirstLastname.isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userFirstLastname);
            }
            if (userSecondLastname != null && !userSecondLastname.isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userSecondLastname);
            }
            if (userThirdLastname != null && !userThirdLastname.isEmpty() && !userThirdLastname.trim().isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userThirdLastname);
            }

            return fullName.toString().trim();
        }

        // Getters y Setters
        public int getUserStateId() {
            return userStateId;
        }

        public void setUserStateId(int userStateId) {
            this.userStateId = userStateId;
        }

        public String getUserGender() {
            return userGender;
        }

        public void setUserGender(String userGender) {
            this.userGender = userGender;
        }

        public String getUserFirstName() {
            return userFirstName;
        }

        public void setUserFirstName(String userFirstName) {
            this.userFirstName = userFirstName;
        }

        public String getUserSecondName() {
            return userSecondName;
        }

        public void setUserSecondName(String userSecondName) {
            this.userSecondName = userSecondName;
        }

        public String getUserThirdName() {
            return userThirdName;
        }

        public void setUserThirdName(String userThirdName) {
            this.userThirdName = userThirdName;
        }

        public String getUserFirstLastname() {
            return userFirstLastname;
        }

        public void setUserFirstLastname(String userFirstLastname) {
            this.userFirstLastname = userFirstLastname;
        }

        public String getUserSecondLastname() {
            return userSecondLastname;
        }

        public void setUserSecondLastname(String userSecondLastname) {
            this.userSecondLastname = userSecondLastname;
        }

        public String getUserThirdLastname() {
            return userThirdLastname;
        }

        public void setUserThirdLastname(String userThirdLastname) {
            this.userThirdLastname = userThirdLastname;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserPhoneNumber() {
            return userPhoneNumber;
        }

        public void setUserPhoneNumber(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }
    }

    // Clase para el estado de la cita
    public static class AppointmentStateDetail {

        @SerializedName("medical_appointment_state_description")
        private String medicalAppointmentStateDescription;

        // Constructor vacío
        public AppointmentStateDetail() {}

        // Getters y Setters
        public String getMedicalAppointmentStateDescription() {
            return medicalAppointmentStateDescription;
        }

        public void setMedicalAppointmentStateDescription(String medicalAppointmentStateDescription) {
            this.medicalAppointmentStateDescription = medicalAppointmentStateDescription;
        }
    }

    // Clase para las prescripciones en la respuesta
    public static class PrescriptionDetail {

        @SerializedName("prescription_notes")
        private String prescriptionNotes;

        @SerializedName("prescription_fecha_emision")
        private String prescriptionFechaEmision;

        @SerializedName("prescription_item")
        private List<PrescriptionItemDetail> prescriptionItem;

        // Constructor vacío
        public PrescriptionDetail() {}

        // Métodos utilitarios
        public int getTotalMedicamentos() {
            return prescriptionItem != null ? prescriptionItem.size() : 0;
        }

        public boolean hasItems() {
            return prescriptionItem != null && !prescriptionItem.isEmpty();
        }

        // Getters y Setters
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

        public List<PrescriptionItemDetail> getPrescriptionItem() {
            return prescriptionItem;
        }

        public void setPrescriptionItem(List<PrescriptionItemDetail> prescriptionItem) {
            this.prescriptionItem = prescriptionItem;
        }
    }

    // Clase para los elementos de prescripción en la respuesta de cita
    public static class PrescriptionItemDetail {

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

        // Constructor vacío
        public PrescriptionItemDetail() {}

        // Método utilitario
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
}