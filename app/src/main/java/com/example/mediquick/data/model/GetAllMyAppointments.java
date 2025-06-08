package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetAllMyAppointments {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<AppointmentData> data;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructors
    public GetAllMyAppointments() {}

    // Main Getters
    public boolean isSuccess() { return success; }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public List<AppointmentData> getData() { return data; }
    public String getTimestamp() { return timestamp; }

    // Main Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setCode(int code) { this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setData(List<AppointmentData> data) { this.data = data; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    // Utility methods
    public boolean hasAppointments() {
        return data != null && !data.isEmpty();
    }

    public int getTotalAppointments() {
        return data != null ? data.size() : 0;
    }

    // Clase interna AppointmentData
    public static class AppointmentData {
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
        private DoctorUser doctorUser;

        @SerializedName("appointment_scheduler")
        private Object appointmentScheduler; // null en los datos

        @SerializedName("patient_user")
        private PatientUser patientUser;

        @SerializedName("branch")
        private Branch branch;

        @SerializedName("medical_appointment_state")
        private MedicalAppointmentState medicalAppointmentState;

        @SerializedName("medical_exam")
        private List<Object> medicalExam; // Array vacío en los datos

        // Constructors
        public AppointmentData() {}

        // Getters
        public String getMedicalAppointmentId() { return medicalAppointmentId; }
        public String getMedicalAppointmentDateTime() { return medicalAppointmentDateTime; }
        public String getMedicalAppointmentCancellationReason() { return medicalAppointmentCancellationReason; }
        public String getMedicalAppointmentNotes() { return medicalAppointmentNotes; }
        public String getMedicalAppointmentCreatedAt() { return medicalAppointmentCreatedAt; }
        public DoctorUser getDoctorUser() { return doctorUser; }
        public Object getAppointmentScheduler() { return appointmentScheduler; }
        public PatientUser getPatientUser() { return patientUser; }
        public Branch getBranch() { return branch; }
        public MedicalAppointmentState getMedicalAppointmentState() { return medicalAppointmentState; }
        public List<Object> getMedicalExam() { return medicalExam; }

        // Setters
        public void setMedicalAppointmentId(String medicalAppointmentId) { this.medicalAppointmentId = medicalAppointmentId; }
        public void setMedicalAppointmentDateTime(String medicalAppointmentDateTime) { this.medicalAppointmentDateTime = medicalAppointmentDateTime; }
        public void setMedicalAppointmentCancellationReason(String medicalAppointmentCancellationReason) { this.medicalAppointmentCancellationReason = medicalAppointmentCancellationReason; }
        public void setMedicalAppointmentNotes(String medicalAppointmentNotes) { this.medicalAppointmentNotes = medicalAppointmentNotes; }
        public void setMedicalAppointmentCreatedAt(String medicalAppointmentCreatedAt) { this.medicalAppointmentCreatedAt = medicalAppointmentCreatedAt; }
        public void setDoctorUser(DoctorUser doctorUser) { this.doctorUser = doctorUser; }
        public void setAppointmentScheduler(Object appointmentScheduler) { this.appointmentScheduler = appointmentScheduler; }
        public void setPatientUser(PatientUser patientUser) { this.patientUser = patientUser; }
        public void setBranch(Branch branch) { this.branch = branch; }
        public void setMedicalAppointmentState(MedicalAppointmentState medicalAppointmentState) { this.medicalAppointmentState = medicalAppointmentState; }
        public void setMedicalExam(List<Object> medicalExam) { this.medicalExam = medicalExam; }

        // Utility methods
        public boolean hasScheduledDateTime() {
            return medicalAppointmentDateTime != null && !medicalAppointmentDateTime.isEmpty();
        }

        public boolean isCreatedState() {
            return medicalAppointmentState != null && medicalAppointmentState.getMedicalAppointmentStateId() == 1;
        }

        public String getDisplayState() {
            return medicalAppointmentState != null ? medicalAppointmentState.getMedicalAppointmentStateDescription() : "Unknown";
        }

        public String getPatientFullName() {
            if (patientUser == null) return "Unknown Patient";
            return patientUser.getUserFirstName() + " " + patientUser.getUserFirstLastname();
        }

        public String getBranchDisplayName() {
            if (branch == null) return "Unknown Branch";
            return branch.getBranchName() + " (" + branch.getBranchAcronym() + ")";
        }
    }

    // Clase interna DoctorUser
    public static class DoctorUser {
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

        // Constructors
        public DoctorUser() {}

        // Getters
        public String getUserId() { return userId; }
        public String getUserFirstName() { return userFirstName; }
        public String getUserSecondName() { return userSecondName; }
        public String getUserThirdName() { return userThirdName; }
        public String getUserFirstLastname() { return userFirstLastname; }
        public String getUserSecondLastname() { return userSecondLastname; }
        public String getUserThirdLastname() { return userThirdLastname; }
        public String getUserEmail() { return userEmail; }
        public String getUserPhoneNumber() { return userPhoneNumber; }

        // Setters
        public void setUserId(String userId) { this.userId = userId; }
        public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }
        public void setUserSecondName(String userSecondName) { this.userSecondName = userSecondName; }
        public void setUserThirdName(String userThirdName) { this.userThirdName = userThirdName; }
        public void setUserFirstLastname(String userFirstLastname) { this.userFirstLastname = userFirstLastname; }
        public void setUserSecondLastname(String userSecondLastname) { this.userSecondLastname = userSecondLastname; }
        public void setUserThirdLastname(String userThirdLastname) { this.userThirdLastname = userThirdLastname; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }

        // Utility methods
        public String getFullName() {
            StringBuilder fullName = new StringBuilder();

            if (userFirstName != null && !userFirstName.isEmpty()) {
                fullName.append(userFirstName);
            }

            if (userSecondName != null && !userSecondName.isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userSecondName);
            }

            if (userFirstLastname != null && !userFirstLastname.isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userFirstLastname);
            }

            if (userSecondLastname != null && !userSecondLastname.isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(userSecondLastname);
            }

            return fullName.toString().trim();
        }

        public String getDisplayName() {
            StringBuilder displayName = new StringBuilder("Dr. ");

            if (userFirstName != null && !userFirstName.isEmpty()) {
                displayName.append(userFirstName);
            }

            if (userFirstLastname != null && !userFirstLastname.isEmpty()) {
                if (displayName.length() > 4) displayName.append(" "); // 4 = "Dr. ".length()
                displayName.append(userFirstLastname);
            }

            return displayName.toString().trim();
        }
    }

    // Clase interna PatientUser
    public static class PatientUser {
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

        // Constructors
        public PatientUser() {}

        // Getters
        public String getUserId() { return userId; }
        public String getUserFirstName() { return userFirstName; }
        public String getUserSecondName() { return userSecondName; }
        public String getUserThirdName() { return userThirdName; }
        public String getUserFirstLastname() { return userFirstLastname; }
        public String getUserSecondLastname() { return userSecondLastname; }
        public String getUserThirdLastname() { return userThirdLastname; }
        public String getUserEmail() { return userEmail; }
        public String getUserPhoneNumber() { return userPhoneNumber; }

        // Setters
        public void setUserId(String userId) { this.userId = userId; }
        public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }
        public void setUserSecondName(String userSecondName) { this.userSecondName = userSecondName; }
        public void setUserThirdName(String userThirdName) { this.userThirdName = userThirdName; }
        public void setUserFirstLastname(String userFirstLastname) { this.userFirstLastname = userFirstLastname; }
        public void setUserSecondLastname(String userSecondLastname) { this.userSecondLastname = userSecondLastname; }
        public void setUserThirdLastname(String userThirdLastname) { this.userThirdLastname = userThirdLastname; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }
    }

    // Clase interna Branch
    public static class Branch {
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
        private Institution institution;

        // Constructors
        public Branch() {}

        // Getters
        public String getBranchId() { return branchId; }
        public String getBranchName() { return branchName; }
        public String getBranchAcronym() { return branchAcronym; }
        public String getBranchDescription() { return branchDescription; }
        public String getBranchFullAddress() { return branchFullAddress; }
        public Institution getInstitution() { return institution; }

        // Setters
        public void setBranchId(String branchId) { this.branchId = branchId; }
        public void setBranchName(String branchName) { this.branchName = branchName; }
        public void setBranchAcronym(String branchAcronym) { this.branchAcronym = branchAcronym; }
        public void setBranchDescription(String branchDescription) { this.branchDescription = branchDescription; }
        public void setBranchFullAddress(String branchFullAddress) { this.branchFullAddress = branchFullAddress; }
        public void setInstitution(Institution institution) { this.institution = institution; }
    }

    // Clase interna Institution
    public static class Institution {
        @SerializedName("institution_id")
        private String institutionId;

        @SerializedName("institution_name")
        private String institutionName;

        @SerializedName("institution_acronym")
        private String institutionAcronym;

        @SerializedName("institution_description")
        private String institutionDescription;

        // Constructors
        public Institution() {}

        // Getters
        public String getInstitutionId() { return institutionId; }
        public String getInstitutionName() { return institutionName; }
        public String getInstitutionAcronym() { return institutionAcronym; }
        public String getInstitutionDescription() { return institutionDescription; }

        // Setters
        public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }
        public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
        public void setInstitutionAcronym(String institutionAcronym) { this.institutionAcronym = institutionAcronym; }
        public void setInstitutionDescription(String institutionDescription) { this.institutionDescription = institutionDescription; }
    }

    // Clase interna MedicalAppointmentState
    public static class MedicalAppointmentState {
        @SerializedName("medical_appointment_state_id")
        private int medicalAppointmentStateId;

        @SerializedName("medical_appointment_state_description")
        private String medicalAppointmentStateDescription;

        // Constructors
        public MedicalAppointmentState() {}

        // Getters
        public int getMedicalAppointmentStateId() { return medicalAppointmentStateId; }
        public String getMedicalAppointmentStateDescription() { return medicalAppointmentStateDescription; }

        // Setters
        public void setMedicalAppointmentStateId(int medicalAppointmentStateId) { this.medicalAppointmentStateId = medicalAppointmentStateId; }
        public void setMedicalAppointmentStateDescription(String medicalAppointmentStateDescription) { this.medicalAppointmentStateDescription = medicalAppointmentStateDescription; }
    }

    @Override
    public String toString() {
        return "GetAllMyAppointments{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", totalAppointments=" + getTotalAppointments() +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}