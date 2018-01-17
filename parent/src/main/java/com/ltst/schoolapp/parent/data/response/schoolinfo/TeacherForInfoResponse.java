package com.ltst.schoolapp.parent.data.response.schoolinfo;


import com.squareup.moshi.Json;

import java.util.List;

public class TeacherForInfoResponse {

    @Json(name = "id")
    public long id;

    @Json(name = "avatar_url")
    public String avatarUrl;

    @Json(name = "first_name")
    public String firstName;

    @Json(name = "last_name")
    public String lastName;

    @Json(name = "email")
    public String email;

    @Json(name = "phone")
    public String phone;

    @Json(name = "additional_phone")
    public String additionalPhone;

    @Json(name = "school_id")
    public long schoolId;

    @Json(name = "group_ids")
    public List<Long> groupIds;

    @Json(name = "layer_identity")
    public String layerIdentity;



}
