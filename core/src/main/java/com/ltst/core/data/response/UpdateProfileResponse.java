package com.ltst.core.data.response;

import com.squareup.moshi.Json;

public class UpdateProfileResponse {

    @Json(name = "first_name")
    private String firstName;

    @Json(name = "last_name")
    private String lastName;

    @Json(name = "email")
    private String email;

    @Json(name = "phone")
    private String phone;

    @Json(name = "avatar_url")
    private String avatar;

    @Json(name = "ic_school")
    private SchoolResponse school;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSchool(SchoolResponse school) {
        this.school = school;
    }

}
