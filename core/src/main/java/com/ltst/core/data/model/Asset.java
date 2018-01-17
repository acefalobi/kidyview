package com.ltst.core.data.model;


import com.ltst.core.data.response.AssetResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 26.09.2016.
 */

public class Asset {
    private String resourceToken;
    private String url;

    public Asset(String resourceToken, String url) {
        this.resourceToken = resourceToken;
        this.url = url;
    }

    public static Asset fromResponse(AssetResponse assetResponse) {
        return new Asset(assetResponse.getResourceToken(), assetResponse.getUrl());
    }

    public static List<String> toTokenList(List<Asset> assets){
        List<String> tokens = new ArrayList<>(assets.size());
        for (Asset asset : assets){
            tokens.add(asset.getResourceToken());
        }
        return tokens;
    }

    public String getResourceToken() {
        return resourceToken;
    }

    public String getUrl() {
        return url;
    }
}
