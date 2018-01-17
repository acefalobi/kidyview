package com.ltst.core.data.request;


import com.ltst.core.data.uimodel.FeedType;

import java.util.HashMap;

/**
 * Created by Danil on 28.09.2016.
 */

public class GetPostsOptions extends HashMap<String, String> {
    private static final String GET_POSTS_DATE = "date";
    private static final String GET_POSTS_PER_PAGE = "per_page";
    private static final String GET_POSTS_AFTER = "after";
    private static final String GET_POSTS_BEFORE = "before";
    private static final String GET_POSTS_FILTER_BY = "filter_by";
    private static final String GET_POSTS_QUERY = "q";
    private static final String GET_POSTS_CHILD_ID = "child_id";
    private static final String GET_POSTS_GROUP_ID = "group_id";

    public GetPostsOptions(String date, Integer perPage, Long after, Integer before,
                           FeedType feedType, Long groupId, Long childId) {
        this(date, perPage, after, before, feedType, null, groupId, childId);
    }

    public GetPostsOptions(String date, Integer perPage, Long after, Integer before,
                           FeedType feedType, String query, Long groupId, Long childId) {
        if (date != null && !date.isEmpty())
            put(GET_POSTS_DATE, date);
        if (perPage != null)
            put(GET_POSTS_PER_PAGE, String.valueOf(perPage));
        if (after != null)
            put(GET_POSTS_AFTER, String.valueOf(after));
        if (before != null)
            put(GET_POSTS_BEFORE, String.valueOf(before));
        if (feedType.getId() != null)
            put(GET_POSTS_FILTER_BY, feedType.getId());
        if (query != null)
            put(GET_POSTS_QUERY, query);
        if (childId != null) {
            put(GET_POSTS_CHILD_ID, String.valueOf(childId));
        }
        if (groupId != null) {
            put(GET_POSTS_GROUP_ID, String.valueOf(groupId));
        }

    }
}
