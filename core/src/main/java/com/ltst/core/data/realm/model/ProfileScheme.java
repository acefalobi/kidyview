package com.ltst.core.data.realm.model;

import com.ltst.core.data.response.ProfileResponse;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class ProfileScheme extends RealmObject {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String additionalPhone;
    private String avatarUrl;
    private SchoolScheme school;

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

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public SchoolScheme getSchool() {
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

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setSchool(SchoolScheme school) {
        this.school = school;
    }

    public static ProfileScheme fromResponce(ProfileResponse profileResponse) {
        ProfileScheme scheme = new ProfileScheme();
        scheme.setEmail(profileResponse.getEmail());
        scheme.setPhone(profileResponse.getPhone());
        scheme.setPhone(profileResponse.getAdditionalPhone());
        scheme.setFirstName(profileResponse.getFirstName());
        scheme.setLastName(profileResponse.getLastName());
        scheme.setAvatarUrl(profileResponse.getAvatarUrl());
        SchoolScheme schoolScheme = SchoolScheme.fromProfileResponse(profileResponse);
        scheme.setSchool(schoolScheme);
        return scheme;
    }

    public void update(ProfileResponse response) {
        setEmail(response.getEmail());
        setPhone(response.getPhone());
        setFirstName(response.getFirstName());
        setLastName(response.getLastName());
        setAvatarUrl(response.getAvatarUrl());
        setAdditionalPhone(response.getAdditionalPhone());
        school.setTitle(response.getSchool().title);
        school.setAddress(response.getSchool().address);
        school.setPhone(response.getSchool().phone);
        school.setEmail(response.getSchool().email);
    }
}
