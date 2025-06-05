// AllDoctorsResponse.java
package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllDoctorsResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<DoctorData> data;

    // Constructor vacío
    public AllDoctorsResponse() {
    }

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

    public List<DoctorData> getData() {
        return data;
    }

    public void setData(List<DoctorData> data) {
        this.data = data;
    }

    // Clase interna para los datos del doctor
    public static class DoctorData {

        @SerializedName("user_id")
        private String userId;

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

        @SerializedName("user_dui")
        private String userDui;

        @SerializedName("user_birthdate")
        private String userBirthdate;

        @SerializedName("user_phone_number")
        private String userPhoneNumber;

        @SerializedName("user_address")
        private String userAddress;

        @SerializedName("user_state")
        private UserState userState;

        @SerializedName("auth_user_profile")
        private List<AuthUserProfile> authUserProfile;

        // Constructor vacío
        public DoctorData() {
        }

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

        // Método utilitario para mostrar en Spinner
        @Override
        public String toString() {
            return "Dr. " + getFullName();
        }

        // Getters y Setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getUserDui() {
            return userDui;
        }

        public void setUserDui(String userDui) {
            this.userDui = userDui;
        }

        public String getUserBirthdate() {
            return userBirthdate;
        }

        public void setUserBirthdate(String userBirthdate) {
            this.userBirthdate = userBirthdate;
        }

        public String getUserPhoneNumber() {
            return userPhoneNumber;
        }

        public void setUserPhoneNumber(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }

        public String getUserAddress() {
            return userAddress;
        }

        public void setUserAddress(String userAddress) {
            this.userAddress = userAddress;
        }

        public UserState getUserState() {
            return userState;
        }

        public void setUserState(UserState userState) {
            this.userState = userState;
        }

        public List<AuthUserProfile> getAuthUserProfile() {
            return authUserProfile;
        }

        public void setAuthUserProfile(List<AuthUserProfile> authUserProfile) {
            this.authUserProfile = authUserProfile;
        }
    }

    // Clase para el estado del usuario
    public static class UserState {

        @SerializedName("user_state_id")
        private int userStateId;

        @SerializedName("user_state_name")
        private String userStateName;

        @SerializedName("user_state_description")
        private String userStateDescription;

        // Constructor vacío
        public UserState() {
        }

        // Getters y Setters
        public int getUserStateId() {
            return userStateId;
        }

        public void setUserStateId(int userStateId) {
            this.userStateId = userStateId;
        }

        public String getUserStateName() {
            return userStateName;
        }

        public void setUserStateName(String userStateName) {
            this.userStateName = userStateName;
        }

        public String getUserStateDescription() {
            return userStateDescription;
        }

        public void setUserStateDescription(String userStateDescription) {
            this.userStateDescription = userStateDescription;
        }
    }

    // Clase para el perfil de autenticación
    public static class AuthUserProfile {

        @SerializedName("security_profile")
        private SecurityProfile securityProfile;

        // Constructor vacío
        public AuthUserProfile() {
        }

        // Getters y Setters
        public SecurityProfile getSecurityProfile() {
            return securityProfile;
        }

        public void setSecurityProfile(SecurityProfile securityProfile) {
            this.securityProfile = securityProfile;
        }
    }

    // Clase para el perfil de seguridad
    public static class SecurityProfile {

        @SerializedName("security_profile_profile_name")
        private String securityProfileProfileName;

        @SerializedName("security_profile_profile_description")
        private String securityProfileProfileDescription;

        // Constructor vacío
        public SecurityProfile() {
        }

        // Getters y Setters
        public String getSecurityProfileProfileName() {
            return securityProfileProfileName;
        }

        public void setSecurityProfileProfileName(String securityProfileProfileName) {
            this.securityProfileProfileName = securityProfileProfileName;
        }

        public String getSecurityProfileProfileDescription() {
            return securityProfileProfileDescription;
        }

        public void setSecurityProfileProfileDescription(String securityProfileProfileDescription) {
            this.securityProfileProfileDescription = securityProfileProfileDescription;
        }
    }
}