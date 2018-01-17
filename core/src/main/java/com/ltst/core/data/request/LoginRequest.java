package com.ltst.core.data.request;

import com.squareup.moshi.Json;

public class LoginRequest {
    @Json(name = "email")
    private String email;

    @Json(name = "password")
    private String password;

    @Json(name = "device")
    Device device;

    public LoginRequest(String email, String password, String deviceToken) {
        this.email = email;
        this.password = password;
        this.device = new Device(deviceToken);
    }

    public static class Device {

        @Json(name = "device_type")
        String deviceType;

        @Json(name = "device_token")
        String deviceToken;

        public Device(String deviceToken) {
            this.deviceToken = deviceToken;
            this.deviceType = "android";
        }
    }
}
