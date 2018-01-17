package com.ltst.core.data.request;

import com.squareup.moshi.Json;

public class ResetPasswordRequest {
    @Json(name = "email")
    String email;

    public ResetPasswordRequest(String email) {
        this.email = email;
    }
}
