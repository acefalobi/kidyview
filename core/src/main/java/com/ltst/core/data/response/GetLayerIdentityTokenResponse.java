package com.ltst.core.data.response;


import com.squareup.moshi.Json;

public class GetLayerIdentityTokenResponse {
    @Json(name = "identity_token")
    public String identityToken;

    public GetLayerIdentityTokenResponse(String identityToken) {
        this.identityToken = identityToken;
    }
}
