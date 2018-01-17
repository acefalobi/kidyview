package com.ltst.core.data.response;


import com.squareup.moshi.Json;

public class ShortUrlResponse {

    @Json(name = "short_url")
    public String shortUrl;
}
