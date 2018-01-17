package com.ltst.core.data.request;

import com.ltst.core.data.model.Group;
import com.squareup.moshi.Json;

public class GroupUpdateRequest {

    @Json(name = "title")
    private String title;

    public GroupUpdateRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static GroupUpdateRequest fromGroup(Group group){
        return new GroupUpdateRequest(group.getTitle());
    }

    public static GroupUpdateRequest fromTitle(String title){
        return new GroupUpdateRequest(title);
    }
}
