package com.ltst.core.data.request;

import com.squareup.moshi.Json;

public class GenerateCodeRequest {



    @Json(name = "group_id")
    long groupId;

    @Json(name = "school_id")
    long schoolId;

    @Json(name = "status")
    String status;

    @Json(name= "first_name")
    String firstName;

    @Json(name = "last_name")
    String lastName;

    @Json(name = "time")
    String time;

    public GenerateCodeRequest(long groupId, long schoolId, String status, String firstName, String lastName, String time) {
        this.groupId = groupId;
        this.schoolId = schoolId;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.time = time;
    }
}
