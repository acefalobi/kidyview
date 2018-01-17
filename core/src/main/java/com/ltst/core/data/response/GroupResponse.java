package com.ltst.core.data.response;

import com.squareup.moshi.Json;

/**
 * Created by Danil on 14.09.2016.
 */
public class GroupResponse {
    @Json(name = "id")
    public long serverId;

    @Json(name = "title")
    public String title;

    @Json(name = "avatar_url")
    public String avatarUrl;
}