
package com.ltst.core.data.response;

import com.squareup.moshi.Json;


public class ChildInGroupResponse {

    @Json(name = "child_id")
    private Long childId;

    @Json(name = "first_name")
    private String firstName;

    @Json(name = "last_name")
    private String lastName;

    @Json(name = "child_avatar_url")
    private String childAvatarUrl;


    @Json(name = "group_id")
    private Long groupId;

    @Json(name = "group_title")
    private String groupTitle;

    @Json(name = "group_avatar_url")
    private String groupAvatarUrl;



    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long child_id) {
        childId = child_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String first_name) {
        firstName = first_name;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long group_id) {
        groupId = group_id;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String group_title) {
        groupTitle = group_title;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String last_name) {
        lastName = last_name;
    }

    public String getChildAvatarUrl() {
        return childAvatarUrl;
    }

    public String getGroupAvatarUrl() {
        return groupAvatarUrl;
    }
}
