package com.example.mediquick.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetUserDataResponse {

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

    // Constructors
    public GetUserDataResponse() {}

    // Getters
    public String getUserId() { return userId; }
    public String getUserGender() { return userGender; }
    public String getUserFirstName() { return userFirstName; }
    public String getUserSecondName() { return userSecondName; }
    public String getUserThirdName() { return userThirdName; }
    public String getUserFirstLastname() { return userFirstLastname; }
    public String getUserSecondLastname() { return userSecondLastname; }
    public String getUserThirdLastname() { return userThirdLastname; }
    public String getUserEmail() { return userEmail; }
    public String getUserDui() { return userDui; }
    public String getUserBirthdate() { return userBirthdate; }
    public String getUserPhoneNumber() { return userPhoneNumber; }
    public String getUserAddress() { return userAddress; }
    public UserState getUserState() { return userState; }
    public List<AuthUserProfile> getAuthUserProfile() { return authUserProfile; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserGender(String userGender) { this.userGender = userGender; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }
    public void setUserSecondName(String userSecondName) { this.userSecondName = userSecondName; }
    public void setUserThirdName(String userThirdName) { this.userThirdName = userThirdName; }
    public void setUserFirstLastname(String userFirstLastname) { this.userFirstLastname = userFirstLastname; }
    public void setUserSecondLastname(String userSecondLastname) { this.userSecondLastname = userSecondLastname; }
    public void setUserThirdLastname(String userThirdLastname) { this.userThirdLastname = userThirdLastname; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setUserDui(String userDui) { this.userDui = userDui; }
    public void setUserBirthdate(String userBirthdate) { this.userBirthdate = userBirthdate; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }
    public void setUserAddress(String userAddress) { this.userAddress = userAddress; }
    public void setUserState(UserState userState) { this.userState = userState; }
    public void setAuthUserProfile(List<AuthUserProfile> authUserProfile) { this.authUserProfile = authUserProfile; }

    // Métodos de utilidad
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();

        if (userFirstName != null && !userFirstName.isEmpty()) {
            fullName.append(userFirstName);
        }

        if (userSecondName != null && !userSecondName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(userSecondName);
        }

        if (userThirdName != null && !userThirdName.isEmpty()) {
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

        if (userThirdLastname != null && !userThirdLastname.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(userThirdLastname);
        }

        return fullName.toString().trim();
    }

    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder();

        if (userFirstName != null && !userFirstName.isEmpty()) {
            displayName.append(userFirstName);
        }

        if (userFirstLastname != null && !userFirstLastname.isEmpty()) {
            if (displayName.length() > 0) displayName.append(" ");
            displayName.append(userFirstLastname);
        }

        return displayName.toString().trim();
    }

    public boolean isActive() {
        return userState != null && userState.getUserStateId() == 1;
    }

    public boolean isPatient() {
        if (authUserProfile == null || authUserProfile.isEmpty()) {
            return false;
        }

        for (AuthUserProfile profile : authUserProfile) {
            if (profile.getSecurityProfile() != null &&
                    "PACIENTE".equals(profile.getSecurityProfile().getSecurityProfileProfileName())) {
                return true;
            }
        }
        return false;
    }

    // Clase interna UserState
    public static class UserState {
        @SerializedName("user_state_id")
        private int userStateId;

        @SerializedName("user_state_name")
        private String userStateName;

        @SerializedName("user_state_description")
        private String userStateDescription;

        // Constructors
        public UserState() {}

        // Getters
        public int getUserStateId() { return userStateId; }
        public String getUserStateName() { return userStateName; }
        public String getUserStateDescription() { return userStateDescription; }

        // Setters
        public void setUserStateId(int userStateId) { this.userStateId = userStateId; }
        public void setUserStateName(String userStateName) { this.userStateName = userStateName; }
        public void setUserStateDescription(String userStateDescription) { this.userStateDescription = userStateDescription; }
    }

    // Clase interna AuthUserProfile
    public static class AuthUserProfile {
        @SerializedName("security_profile")
        private SecurityProfile securityProfile;

        // Constructors
        public AuthUserProfile() {}

        // Getters
        public SecurityProfile getSecurityProfile() { return securityProfile; }

        // Setters
        public void setSecurityProfile(SecurityProfile securityProfile) { this.securityProfile = securityProfile; }
    }

    // Clase interna SecurityProfile
    public static class SecurityProfile {
        @SerializedName("security_profile_profile_name")
        private String securityProfileProfileName;

        @SerializedName("security_profile_profile_description")
        private String securityProfileProfileDescription;

        // Constructors
        public SecurityProfile() {}

        // Getters
        public String getSecurityProfileProfileName() { return securityProfileProfileName; }
        public String getSecurityProfileProfileDescription() { return securityProfileProfileDescription; }

        // Setters
        public void setSecurityProfileProfileName(String securityProfileProfileName) {
            this.securityProfileProfileName = securityProfileProfileName;
        }
        public void setSecurityProfileProfileDescription(String securityProfileProfileDescription) {
            this.securityProfileProfileDescription = securityProfileProfileDescription;
        }
    }

    @Override
    public String toString() {
        return "GetUserDataResponse{" +
                "userId='" + userId + '\'' +
                ", userGender='" + userGender + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userDui='" + userDui + '\'' +
                ", userState=" + (userState != null ? userState.getUserStateName() : "null") +
                ", isPatient=" + isPatient() +
                '}';
    }
}