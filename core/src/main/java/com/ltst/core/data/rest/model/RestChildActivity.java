package com.ltst.core.data.rest.model;

import com.squareup.moshi.Json;

/**
 * Created by Danil on 26.09.2016.
 */

public class RestChildActivity {
    private int id;
    private String title;
    @Json(name = "icon_url")
    private String iconUrl;

    public RestChildActivity(int id, String title, String iconUrl) {
        this.id = id;
        this.title = title;
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
