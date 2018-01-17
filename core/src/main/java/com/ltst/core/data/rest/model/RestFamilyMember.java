package com.ltst.core.data.rest.model;

import com.squareup.moshi.Json;

public class RestFamilyMember {
    private Integer id;
    @Json(name = "first_name")
    private String firstName;
    @Json(name = "last_name")
    private String lastName;
    private String email;
    private String phone;
    @Json(name = "additional_phone")
    private String additionalPhone;
    @Json(name = "avatar_url")
    private String avatarUrl;

    public RestFamilyMember(Integer id, String firstName, String lastName, String email,
                            String phone, String additionalPhone, String avatarUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.additionalPhone = additionalPhone;
        this.avatarUrl = avatarUrl;
    }

    public Integer getId() {
        return id;
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

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
