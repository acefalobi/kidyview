package com.ltst.core.data.rest.model;

import com.squareup.moshi.Json;

/**
 * Created by Danil on 22.09.2016.
 */

public class RestImage {
    @Json(name = "resource_token")
    private String resourceToken;
    private String url;

    public RestImage(String resourceToken, String url) {
        this.resourceToken = resourceToken;
        this.url = url;
    }

    public String getResourceToken() {
        return resourceToken;
    }

    public String getUrl() {
        return url;
    }
}
