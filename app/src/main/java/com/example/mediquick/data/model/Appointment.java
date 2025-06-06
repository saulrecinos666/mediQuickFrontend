package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

public class Appointment {
    private String medical_appointment_id;
    private String medical_appointment_date_time;
    private String medical_appointment_cancellation_reason;
    private String medical_appointment_notes;
    private String medical_appointment_created_at;

    // ✅ Objetos anidados
    private PatientUser patient_user;
    private DoctorUser doctor_user;
    private AppointmentScheduler appointment_scheduler;
    private Branch branch;
    private MedicalAppointmentState medical_appointment_state;

    // ✅ Getters principales
    public String getMedical_appointment_id() {
        return medical_appointment_id;
    }

    public String getMedical_appointment_date_time() {
        return medical_appointment_date_time;
    }

    public String getMedical_appointment_created_at() {
        return medical_appointment_created_at;
    }

    // ✅ Getter para patient_user_id (extraído del objeto anidado)
    public String getPatient_user_id() {
        return patient_user != null ? patient_user.getUser_id() : null;
    }

    // ✅ Getter para doctor_user_id (extraído del objeto anidado)
    public String getDoctor_user_id() {
        return doctor_user != null ? doctor_user.getUser_id() : null;
    }

    // ✅ Getters para objetos completos
    public PatientUser getPatient_user() { return patient_user; }
    public DoctorUser getDoctor_user() { return doctor_user; }
    public Branch getBranch() { return branch; }
    public MedicalAppointmentState getMedical_appointment_state() { return medical_appointment_state; }

    // ✅ Clases anidadas para los objetos
    public static class PatientUser {
        private String user_id;
        private String user_first_name;
        private String user_second_name;
        private String user_first_lastname;
        private String user_second_lastname;
        private String user_email;
        private String user_phone_number;

        public String getUser_id() { return user_id; }
        public String getUser_first_name() { return user_first_name; }
        public String getUser_second_name() { return user_second_name; }
        public String getUser_first_lastname() { return user_first_lastname; }
        public String getUser_second_lastname() { return user_second_lastname; }
        public String getUser_email() { return user_email; }
        public String getUser_phone_number() { return user_phone_number; }

        // ✅ Método helper para nombre completo
        public String getFullName() {
            StringBuilder name = new StringBuilder();
            if (user_first_name != null) name.append(user_first_name);
            if (user_second_name != null && !user_second_name.isEmpty()) {
                name.append(" ").append(user_second_name);
            }
            if (user_first_lastname != null) name.append(" ").append(user_first_lastname);
            if (user_second_lastname != null && !user_second_lastname.isEmpty()) {
                name.append(" ").append(user_second_lastname);
            }
            return name.toString().trim();
        }
    }

    public static class DoctorUser {
        private String user_id;
        private String user_first_name;
        private String user_first_lastname;

        public String getUser_id() { return user_id; }
        public String getUser_first_name() { return user_first_name; }
        public String getUser_first_lastname() { return user_first_lastname; }
    }

    public static class AppointmentScheduler {
        private String scheduler_id;
        private String scheduler_name;

        public String getScheduler_id() { return scheduler_id; }
        public String getScheduler_name() { return scheduler_name; }
    }

    public static class Branch {
        private String branch_id;
        private String branch_name;
        private String branch_acronym;
        private String branch_full_address;
        private Institution institution;

        public String getBranch_id() { return branch_id; }
        public String getBranch_name() { return branch_name; }
        public String getBranch_acronym() { return branch_acronym; }
        public String getBranch_full_address() { return branch_full_address; }
        public Institution getInstitution() { return institution; }

        public static class Institution {
            private String institution_id;
            private String institution_name;
            private String institution_acronym;

            public String getInstitution_id() { return institution_id; }
            public String getInstitution_name() { return institution_name; }
            public String getInstitution_acronym() { return institution_acronym; }
        }
    }

    public static class MedicalAppointmentState {
        private int medical_appointment_state_id;
        private String medical_appointment_state_description;

        public int getMedical_appointment_state_id() { return medical_appointment_state_id; }
        public String getMedical_appointment_state_description() { return medical_appointment_state_description; }
    }

    // ✅ Setters principales (si los necesitas)
    public void setMedical_appointment_id(String id) { this.medical_appointment_id = id; }
    public void setMedical_appointment_date_time(String dateTime) { this.medical_appointment_date_time = dateTime; }
    public void setPatient_user(PatientUser patient_user) { this.patient_user = patient_user; }
    public void setDoctor_user(DoctorUser doctor_user) { this.doctor_user = doctor_user; }
    public void setBranch(Branch branch) { this.branch = branch; }
}