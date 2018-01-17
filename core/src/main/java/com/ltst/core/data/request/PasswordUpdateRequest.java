package com.ltst.core.data.request;

import com.squareup.moshi.Json;

public class PasswordUpdateRequest {

    @Json(name = "new_password")
    private String newPassword;
    @Json(name = "current_password")
    private String currentPassword;

    public PasswordUpdateRequest(String newPassword, String currentPassword) {
        this.newPassword = newPassword;
        this.currentPassword = currentPassword;
    }
}
