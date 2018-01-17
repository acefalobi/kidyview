package com.ltst.schoolapp.parent.data.response;


import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.rest.model.RestChildActivity;
import com.ltst.core.data.rest.model.RestImage;
import com.squareup.moshi.Json;

import java.util.List;

public class RestPost {

    private int id;
    @Json(name = "activity")
    private RestChildActivity activity;
    @Json(name = "kind")
    private String kind;
    @Json(name = "child_id")
    private int childId;
    private List<RestImage> images;
    private String content;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "children")
    private List<ChildResponse> childResponse;
    //    @Json(name = "group")
//    GroupResponse group;
    @Json(name = "post_avatar_url")
    String postAvatarUrl;

    @Json(name = "post_title")
    String postTitle;

    /*for checkout report*/
    @Json(name = "child_avatar_url")
    String childAvatarUrl;
    @Json(name = "child_first_name")
    String firstName;
    @Json(name = "child_last_name")
    String lastName;


    public RestPost(int id, RestChildActivity activity, String kind, int childId,
                    List<RestImage> images, String content, String createdAt,
                    List<ChildResponse> childResponse, String postAvatarUrl, String postTitle) {
        this.id = id;
        this.activity = activity;
        this.kind = kind;
        this.childId = childId;
        this.images = images;
        this.content = content;
        this.createdAt = createdAt;
        this.childResponse = childResponse;
        this.postAvatarUrl = postAvatarUrl;
        this.postTitle = postTitle;
    }

    public String getChildAvatarUrl() {
        return childAvatarUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPostAvatarUrl() {
        return postAvatarUrl;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public int getId() {
        return id;
    }

    public RestChildActivity getActivity() {
        return activity;
    }

    public String getKind() {
        return kind;
    }

    public int getChildId() {
        return childId;
    }

    public List<RestImage> getImages() {
        return images;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<ChildResponse> getChildResponse() {
        return childResponse;
    }
}
