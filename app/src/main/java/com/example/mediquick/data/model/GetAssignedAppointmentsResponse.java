// GetAssignedAppointmentsResponse.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetAssignedAppointmentsResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<AssignedAppointmentData> data;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor vacío
    public GetAssignedAppointmentsResponse() {}

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

    public List<AssignedAppointmentData> getData() {
        return data;
    }

    public void setData(List<AssignedAppointmentData> data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Clase para los datos de la cita asignada
    public static class AssignedAppointmentData {

        @SerializedName("medical_appointment_id")
        private String medicalAppointmentId;

        @SerializedName("medical_appointment_date_time")
        private String medicalAppointmentDateTime;

        @SerializedName("medical_appointment_cancellation_reason")
        private String medicalAppointmentCancellationReason;

        @SerializedName("medical_appointment_notes")
        private String medicalAppointmentNotes;

        @SerializedName("medical_appointment_created_at")
        private String medicalAppointmentCreatedAt;

        @SerializedName("doctor_user")
        private UserInfo doctorUser;

        @SerializedName("appointment_scheduler")
        private UserInfo appointmentScheduler;

        @SerializedName("patient_user")
        private UserInfo patientUser;

        @SerializedName("branch")
        private BranchInfo branch;

        @SerializedName("medical_appointment_state")
        private AppointmentState medicalAppointmentState;

        @SerializedName("medical_exam")
        private List<Object> medicalExam; // Lista vacía en el ejemplo, puedes cambiar Object por el tipo correcto

        // Constructor vacío
        public AssignedAppointmentData() {}

        // Métodos utilitarios
        public String getDoctorFullName() {
            if (doctorUser != null) {
                return doctorUser.getFullName();
            }
            return "Doctor no asignado";
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

        // Getters y Setters
        public String getMedicalAppointmentId() {
            return medicalAppointmentId;
        }

        public void setMedicalAppointmentId(String medicalAppointmentId) {
            this.medicalAppointmentId = medicalAppointmentId;
        }

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

        public UserInfo getDoctorUser() {
            return doctorUser;
        }

        public void setDoctorUser(UserInfo doctorUser) {
            this.doctorUser = doctorUser;
        }

        public UserInfo getAppointmentScheduler() {
            return appointmentScheduler;
        }

        public void setAppointmentScheduler(UserInfo appointmentScheduler) {
            this.appointmentScheduler = appointmentScheduler;
        }

        public UserInfo getPatientUser() {
            return patientUser;
        }

        public void setPatientUser(UserInfo patientUser) {
            this.patientUser = patientUser;
        }

        public BranchInfo getBranch() {
            return branch;
        }

        public void setBranch(BranchInfo branch) {
            this.branch = branch;
        }

        public AppointmentState getMedicalAppointmentState() {
            return medicalAppointmentState;
        }

        public void setMedicalAppointmentState(AppointmentState medicalAppointmentState) {
            this.medicalAppointmentState = medicalAppointmentState;
        }

        public List<Object> getMedicalExam() {
            return medicalExam;
        }

        public void setMedicalExam(List<Object> medicalExam) {
            this.medicalExam = medicalExam;
        }
    }

    // Clase para información de usuario (doctor, paciente, scheduler)
    public static class UserInfo {

        @SerializedName("user_id")
        private String userId;

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
        public UserInfo() {}

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
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

    // Clase para información de la sucursal
    public static class BranchInfo {

        @SerializedName("branch_id")
        private String branchId;

        @SerializedName("branch_name")
        private String branchName;

        @SerializedName("branch_acronym")
        private String branchAcronym;

        @SerializedName("branch_description")
        private String branchDescription;

        @SerializedName("branch_full_address")
        private String branchFullAddress;

        @SerializedName("institution")
        private InstitutionInfo institution;

        // Constructor vacío
        public BranchInfo() {}

        // Getters y Setters
        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
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

        public String getBranchFullAddress() {
            return branchFullAddress;
        }

        public void setBranchFullAddress(String branchFullAddress) {
            this.branchFullAddress = branchFullAddress;
        }

        public InstitutionInfo getInstitution() {
            return institution;
        }

        public void setInstitution(InstitutionInfo institution) {
            this.institution = institution;
        }
    }

    // Clase para información de la institución
    public static class InstitutionInfo {

        @SerializedName("institution_id")
        private String institutionId;

        @SerializedName("institution_name")
        private String institutionName;

        @SerializedName("institution_acronym")
        private String institutionAcronym;

        @SerializedName("institution_description")
        private String institutionDescription;

        // Constructor vacío
        public InstitutionInfo() {}

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

    // Clase para el estado de la cita
    public static class AppointmentState {

        @SerializedName("medical_appointment_state_id")
        private int medicalAppointmentStateId;

        @SerializedName("medical_appointment_state_description")
        private String medicalAppointmentStateDescription;

        // Constructor vacío
        public AppointmentState() {}

        // Getters y Setters
        public int getMedicalAppointmentStateId() {
            return medicalAppointmentStateId;
        }

        public void setMedicalAppointmentStateId(int medicalAppointmentStateId) {
            this.medicalAppointmentStateId = medicalAppointmentStateId;
        }

        public String getMedicalAppointmentStateDescription() {
            return medicalAppointmentStateDescription;
        }

        public void setMedicalAppointmentStateDescription(String medicalAppointmentStateDescription) {
            this.medicalAppointmentStateDescription = medicalAppointmentStateDescription;
        }
    }
}