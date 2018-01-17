package com.ltst.core.data.request;

import com.squareup.moshi.Json;

public class RecoveryPasswordRequest {

    @Json(name = "email")
    String email;

    @Json(name = "password")
    String password;

    @Json(name = "reset_password_code")
    String code;

    @Json(name = "device")
    LoginRequest.Device device;

    public RecoveryPasswordRequest(String email, String password, String code, String deviceToken) {
        this.email = email;
        this.password = password;
        this.code = code;
        this.device = new LoginRequest.Device(deviceToken);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCode() {
        return code;
    }

    public LoginRequest.Device getDevice() {
        return device;
    }

    public void setDevice(LoginRequest.Device device) {
        this.device = device;
    }
}
