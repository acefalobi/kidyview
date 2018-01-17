package com.ltst.core.data.realm.model;


import com.ltst.core.data.response.ProfileResponse;
import com.ltst.core.data.response.SchoolResponse;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class SchoolScheme extends RealmObject {

    private String address;
    private String title;
    private String phone;
    private String email;
    private String additionalPhone;

    public String getAddress() {
        return address;
    }

    public String getTitle() {
        return title;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public static SchoolScheme fromProfileResponse(ProfileResponse profileResponse) {
        SchoolScheme schoolScheme = new SchoolScheme();
        SchoolResponse schoolResponse = profileResponse.getSchool();
        if (schoolResponse != null) {
            schoolScheme.setTitle(schoolResponse.title);
            schoolScheme.setAddress(schoolResponse.address);
            schoolScheme.setPhone(schoolResponse.phone);
            schoolScheme.setEmail(schoolResponse.email);
        }
        return schoolScheme;
    }
}
