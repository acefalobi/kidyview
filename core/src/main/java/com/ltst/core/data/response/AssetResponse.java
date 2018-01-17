package com.ltst.core.data.response;

import com.squareup.moshi.Json;

/**
 * Created by Danil on 26.09.2016.
 */

public class AssetResponse {
    @Json(name = "resource_token")
    private String resourceToken;
    private String url;

    public AssetResponse(String resourceToken, String url) {
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
