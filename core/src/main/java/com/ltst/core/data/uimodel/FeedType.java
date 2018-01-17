package com.ltst.core.data.uimodel;

/**
 * Created by Danil on 21.09.2016.
 */

public enum FeedType {
    FEED(null, "All Feed"),
    GROUP_FEED("group_posts", "Group Feed"),
    CHILDREN_FEED("children_posts", "Children Feed");


    private final String id;
    private final String name;

    FeedType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
