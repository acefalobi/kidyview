package com.ltst.schoolapp.parent.data.response.schoolinfo;


import com.ltst.core.data.response.SchoolResponse;
import com.squareup.moshi.Json;

public class SchoolForInfoResponse extends SchoolResponse {

    @Json(name = "id")
    public long id;

    @Json(name = "avatar_url")
    public String avatarUrl;

}
