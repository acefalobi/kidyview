package com.ltst.core.data.response;

import com.squareup.moshi.Json;

import java.util.List;

public class DatesResponse {
    @Json(name = "dates")
    public List<String> dates;

}
