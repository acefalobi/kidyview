package com.ltst.core.data.request;

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

public class ProfileRequest {
    @Json(name = "email")
    private final String email;

    @Json(name = "password")
    private final String password;

    @Json(name = "device")
    Device device;

    @Json(name = "code")
    String code;

    public ProfileRequest(String email, String password, String deviceToken, @Nullable String code) {
        this.email = email;
        this.password = password;
        this.code = code;
        this.device = new Device(deviceToken);
    }

    static class Device {

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
