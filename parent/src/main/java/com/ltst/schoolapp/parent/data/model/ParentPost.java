package com.ltst.schoolapp.parent.data.model;

import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.model.Image;
import com.ltst.core.data.model.Post;
import com.ltst.schoolapp.parent.data.response.RestPost;

import java.util.List;

public class ParentPost {

    public static Post fromRestPost(RestPost restPost, Group group) {
        int imageCount = restPost.getImages() == null ? 0 : restPost.getImages().size();
        int type;
        if (restPost.getKind().equals(Post.CHECKOUT_REPORT_TYPE)) {
            type = Post.CHECKOUT_REPORT;
        } else {
            switch (imageCount) {
                case 0:
                    type = Post.WITH_ACTIVITY;
                    break;
                case 1:
                    type = Post.WITH_ONE_PHOTO;
                    break;
                case 2:
                    type = Post.WITH_TWO_PHOTO;
                    break;
                default:
                case 3:
                    type = Post.WITH_TREE_PHOTO;
                    break;
            }
        }

        String postAvatarUrl = restPost.getPostAvatarUrl();
        if (postAvatarUrl == null) {
            String kind = restPost.getKind();
            if (kind.equals(Post.CHILD_POST_TYPE) || kind.equals(Post.CHECKOUT_REPORT_TYPE)) {
                postAvatarUrl = Post.CHILD_AVATAR_HOLDER;
            } else {
                postAvatarUrl = Post.GROUP_AVATAR_HOLDER;
            }
        }
        String postTitle = restPost.getPostTitle();
        ChildActivity childActivity = null;
        if (restPost.getActivity() != null) {
            childActivity = ChildActivity.fromRest(restPost.getActivity());
        }
        List<Image> imageList = null;
        if (restPost.getImages() != null) {
            imageList = Image.fromRest(restPost.getImages());
        }
        return new Post(
                restPost.getId(),
                postAvatarUrl,
                postTitle,
                childActivity,
                imageList,
                restPost.getContent(),
                restPost.getCreatedAt(),
                type,
                restPost.getChildAvatarUrl(),
                restPost.getFirstName(),
                restPost.getLastName());
    }

    public static Post fromRestPost(RestPost restPost) {
        return fromRestPost(restPost, null);
    }



}
