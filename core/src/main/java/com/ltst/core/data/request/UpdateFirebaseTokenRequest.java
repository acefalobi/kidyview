package com.ltst.core.data.request;


import com.squareup.moshi.Json;

public class UpdateFirebaseTokenRequest {
    @Json(name = "device_type")
    private String deviceType;

    @Json(name = "device_token")
    private String fireBaseToken;

    public UpdateFirebaseTokenRequest(String fireBaseToken) {
        this.fireBaseToken = fireBaseToken;
        this.deviceType = "android";
    }
}
