package com.ltst.core.data.response;


import com.squareup.moshi.Json;

public class TeacherResponse {

    @Json(name = "avatar_url")
    public String avatarUrl;

    @Json(name = "first_name")
    public String firstName;

    @Json(name = "last_name")
    public String lastName;

    @Json(name = "layer_identity")
    public String layerIdentity;
}
