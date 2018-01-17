package com.ltst.core.data.request;

import com.ltst.core.data.model.Asset;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.data.model.PostType;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 26.09.2016.
 */

public class AddPostRequest {
    @Json(name = "activity_id")
    private int activityId;
    private String kind;
    @Json(name = "child_id")
    private long childId;
    @Json(name = "children_ids")
    private List<Long> childrenIds;
    private String content;
    @Json(name = "image_tokens")
    private List<String> imageTokens;

    private AddPostRequest(int activityId, String kind, long childId, List<Long> childrenIds,
                           String content, List<String> imageTokens) {
        this.activityId = activityId;
        this.kind = kind;
        this.childId = childId;
        this.childrenIds = childrenIds;
        this.content = content;
        this.imageTokens = imageTokens;
    }

    public static AddPostRequest create(ChildActivity childActivity,
                                        List<SelectPersonModel> children,
                                        String content,
                                        List<Asset> asset) {
        int activityId = childActivity.getId();
        String kind;
        long childId;
        List<Long> childrenIds;
        List<String> imageTokens = Asset.toTokenList(asset);
        if (children.contains(SelectPersonModel.getEmptyGroup())) {
            kind = PostType.GROUP.toString();
            childId = 0;
            childrenIds = new ArrayList<>();
        } else if (children.size() > 1) {
            kind = PostType.CHILDREN.toString();
            childId = 0;
            childrenIds = SelectPersonModel.getServerIdList(children);
        } else {
            kind = PostType.CHILD.toString();
            childId = children.get(0).getServerId();
            childrenIds = new ArrayList<>();
        }
        return new AddPostRequest(activityId, kind, childId, childrenIds, content, imageTokens);
    }

}
