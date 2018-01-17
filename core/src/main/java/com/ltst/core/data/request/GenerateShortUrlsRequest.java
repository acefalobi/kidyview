package com.ltst.core.data.request;


import com.squareup.moshi.Json;

import java.util.Collection;

public class GenerateShortUrlsRequest {

    @Json(name = "resource_tokens")
    private final Collection<String> urls;

    public GenerateShortUrlsRequest(Collection<String> urls) {
        this.urls = urls;
    }
}
