package com.ltst.core.data.model;

/**
 * Created by Danil on 26.09.2016.
 */

public enum PostType {
    GROUP("group_post"),
    CHILD("child_post"),
    CHILDREN("children_post");

    private String kind;

    PostType(String kind) {
        this.kind = kind;
    }


    @Override
    public String toString() {
        return kind;
    }
}
