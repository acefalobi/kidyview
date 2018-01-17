package com.ltst.schoolapp.parent.data.response.schoolinfo;


import com.squareup.moshi.Json;

import java.util.List;

public class SchoolInfoResponse {

    @Json(name = "schools")
    public List<SchoolForInfoResponse> schools;

    @Json(name = "teachers")
    public List<TeacherForInfoResponse> teachers;

    @Json(name = "children")
    public List<ChildForInfoResponse> children;

}
