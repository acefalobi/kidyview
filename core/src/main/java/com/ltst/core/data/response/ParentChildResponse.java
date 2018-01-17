package com.ltst.core.data.response;

import com.squareup.moshi.Json;

/* response has info about child in specific ic_school for parent app */
public class ParentChildResponse {

    @Json(name = "school_id")
    private int schoolId;

    @Json(name = "school_title")
    private String schoolTitle;

    @Json(name = "school_avatar_url")
    private String schoolAvatar;

    @Json(name = "child_profile")
    private ChildResponse child;

    public ParentChildResponse(int schoolId, String schoolTitle, String schoolAvatar, ChildResponse child) {
        this.schoolId = schoolId;
        this.schoolTitle = schoolTitle;
        this.schoolAvatar = schoolAvatar;
        this.child = child;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public String getSchoolTitle() {
        return schoolTitle;
    }

    public String getSchoolAvatar() {
        return schoolAvatar;
    }

    public ChildResponse getChild() {
        return child;
    }
}
