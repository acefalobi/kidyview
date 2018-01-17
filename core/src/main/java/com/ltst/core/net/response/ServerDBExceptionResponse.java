package com.ltst.core.net.response;

import com.squareup.moshi.Json;

import java.util.List;

public class ServerDBExceptionResponse {

    @Json(name = "error")
    public String error;

    @Json(name = "message")
    public String message;

    @Json(name = "details")
    public Details details;


    public static class Details {

        @Json(name = "users_teacher")
        public UsersTeacher usersTeacher;

    }

    public static class UsersTeacher {
        @Json(name = "email")
        public List<String> emails;
    }

}
