package com.ltst.core.data.response;

import com.squareup.moshi.Json;

public class ProfileResponse {

    @Json(name = "authentication_token")
    String serverToken;

    @Json(name = "first_name")
    private String firstName;

    @Json(name = "last_name")
    private String lastName;

    @Json(name = "email")
    private String email;

    @Json(name = "phone")
    private String phone;

    @Json(name = "additional_phone")
    private String additionalPhone;

    @Json(name = "avatar_url")
    String avatarUrl;

    @Json(name = "school")
    private SchoolResponse school;

    @Json(name = "is_admin")
    private boolean isAdmin;

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public SchoolResponse getSchool() {
        return school;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSchool(SchoolResponse school) {
        this.school = school;
    }

    public String getServerToken() {
        return serverToken;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }
}
