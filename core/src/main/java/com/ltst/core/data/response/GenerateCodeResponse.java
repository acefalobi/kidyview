package com.ltst.core.data.response;

import com.squareup.moshi.Json;

public class    GenerateCodeResponse {

    @Json(name = "code")
    private String code;

    public String getCode() {
        return code;
    }
}
