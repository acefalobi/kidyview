package com.ltst.core.data.rest.model;

import com.ltst.core.data.response.ChildResponse;
import com.squareup.moshi.Json;

import java.util.List;

public class RestChildCheck {
    private Integer id;
    private ChildResponse child;
    private String datetime;
    @Json(name = "child_states")
    private List<RestChildState> childStates;

    public Integer getId() {
        return id;
    }

    public ChildResponse getChild() {
        return child;
    }

    public String getDatetime() {
        return datetime;
    }

    public List<RestChildState> getChildStates() {
        return childStates;
    }
}
