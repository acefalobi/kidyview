package com.ltst.core.data.response;

import com.squareup.moshi.Json;

public class SchoolResponse {

    @Json(name = "address")
    public String address;

    @Json(name = "title")
    public String title;

    @Json(name = "phone")
    public String phone;

    @Json(name = "additional_phone")
    public String additionalPhone;

    @Json(name = "email")
    public String email;

    public String getAddress() {
        return address;
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
}
