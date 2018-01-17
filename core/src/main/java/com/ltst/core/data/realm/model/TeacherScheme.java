package com.ltst.core.data.realm.model;

import io.realm.RealmObject;

public class TeacherScheme extends RealmObject {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

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

    public void setSchool(SchoolScheme school) {
        this.school = school;
    }
}
