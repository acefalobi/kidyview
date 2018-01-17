package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.rest.model.RestPost;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by Danil on 22.09.2016.
 */
public class Post implements Parcelable {

    private long id;
    private String iconUrl;
    private String title;
    private ChildActivity activity;
    private List<Image> images;
    private String content;
    private String createdAt;

    /*for checkout report*/
    private String childAvatarUrl;
    private String childFirstName;
    private String childLastName;


    public static final int CHECKOUT_REPORT = 0;
    public static final String CHECKOUT_REPORT_TYPE = "checkout_report";
    public static final String CHILD_POST_TYPE = "child_post";
    public static final int WITH_ACTIVITY = 1;
    public static final int DATE = 2;
    public static final int WITH_ONE_PHOTO = 3;
    public static final int WITH_TWO_PHOTO = 4;
    public static final int WITH_TREE_PHOTO = 5;

    @IntDef({CHECKOUT_REPORT, WITH_ONE_PHOTO, WITH_ACTIVITY, DATE, WITH_TWO_PHOTO, WITH_TREE_PHOTO})
    @Retention(RetentionPolicy.SOURCE)
    @interface ListType {

    }


    public static final String GROUP_AVATAR_HOLDER = "Post.groupAvatarHolder";
    public static final String CHILD_AVATAR_HOLDER = "Post.childAvatarHolder";
    private int listType;

    public Post(long id, String iconUrl, String title, ChildActivity activity, List<Image> images,
                String content, String createdAt, int listType, String childAvatarUrl, String childFirstName, String childLastName) {
        this.id = id;
        this.iconUrl = iconUrl;
        this.title = title;
        this.activity = activity;
        this.images = images;
        this.content = content;
        this.createdAt = createdAt;
        this.listType = listType;
        this.childAvatarUrl = childAvatarUrl;
        this.childFirstName = childFirstName;
        this.childLastName = childLastName;
    }

    /*used for create PendingIntent in firebase push notification*/
    public Post(long id, String createdAt, String childFirstName, String childLastName,String childAvatarUrl) {
        this.id = id;
        this.createdAt = createdAt;
        this.childFirstName = childFirstName;
        this.childLastName = childLastName;
        this.childAvatarUrl = childAvatarUrl;
    }

    public static Post fromRestPost(RestPost restPost) {
        return fromRestPost(restPost, null);
    }

    public static Post fromRestPost(RestPost restPost, Group group) {
        int type;
        String kind = restPost.getKind();
        if (!kind.equals(CHECKOUT_REPORT_TYPE)) {
            int imageCount = restPost.getImages() == null ? 0 : restPost.getImages().size();

            switch (imageCount) {
                case 0:
                    type = WITH_ACTIVITY;
                    break;
                case 1:
                    type = WITH_ONE_PHOTO;
                    break;
                case 2:
                    type = WITH_TWO_PHOTO;
                    break;
                default:
                case 3:
                    type = WITH_TREE_PHOTO;
                    break;
            }
        } else {
            type = CHECKOUT_REPORT;
        }

        ChildActivity activity = null;
        if (restPost.getActivity() != null) {
            activity = ChildActivity.fromRest(restPost.getActivity());
        }
        return new Post(
                restPost.getId(),
                getIconUrlFromRest(group, restPost.getChildResponse()),
                getTitleFromRest(group, restPost.getChildResponse()),
                activity,
                Image.fromRest(restPost.getImages()),
                restPost.getContent(),
                restPost.getCreatedAt(),
                type,
                restPost.getChildAvatarUrl(),
                restPost.getFirstName(),
                restPost.getLastName());
    }

    public String getChildAvatarUrl() {
        return childAvatarUrl;
    }

    public String getChildFirstName() {
        return childFirstName;
    }

    public String getChildLastName() {
        return childLastName;
    }

    public static String getIconUrlFromRest(Group group, List<ChildResponse> childResponses) {
        String iconUrl = null;
        if (childResponses != null) {
            int childSize = childResponses.size();
            if (childSize == 0 || childSize > 1) {
                if (group != null && !StringUtils.isBlank(group.getAvatarUrl())) {
                    iconUrl = group.getAvatarUrl();
                } else {
                    iconUrl = GROUP_AVATAR_HOLDER;
                }

            } else if (childSize == 1) {
                iconUrl = childResponses.get(0).getAvatarUrl() == null
                        ? CHILD_AVATAR_HOLDER
                        : childResponses.get(0).getAvatarUrl();
            }
        }
        return iconUrl;
    }

    private static final String GROUP_TITLE_PREFIX = "Group: ";

    public static String getTitleFromRest(Group group, List<ChildResponse> childResponses) {
        StringBuilder builder = new StringBuilder();
        if (childResponses != null) {
            if (childResponses.size() == 0) {
                return group == null ? null : GROUP_TITLE_PREFIX + group.getTitle();
            }
            for (int x = 0; x < childResponses.size(); x++) {
                ChildResponse childResponse = childResponses.get(x);
                builder.append(childResponse.getFirstName());
                builder.append(StringUtils.SPACE);
                builder.append(childResponse.getLastName());
                if (x != childResponses.size() - 1) {
                    builder.append(StringUtils.COMMA);
                    builder.append(StringUtils.SPACE);
                }
            }
        }

        return builder.toString();
    }

    public long getId() {
        return id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public ChildActivity getActivity() {
        return activity;
    }

    public List<Image> getImages() {
        return images;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getListType() {
        return listType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return id == post.id;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.iconUrl);
        dest.writeString(this.title);
        dest.writeParcelable(this.activity, flags);
        dest.writeTypedList(this.images);
        dest.writeString(this.content);
        dest.writeString(this.createdAt);
        dest.writeString(this.childAvatarUrl);
        dest.writeString(this.childFirstName);
        dest.writeString(this.childLastName);
        dest.writeInt(this.listType);
    }

    protected Post(Parcel in) {
        this.id = in.readLong();
        this.iconUrl = in.readString();
        this.title = in.readString();
        this.activity = in.readParcelable(ChildActivity.class.getClassLoader());
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.content = in.readString();
        this.createdAt = in.readString();
        this.childAvatarUrl = in.readString();
        this.childFirstName = in.readString();
        this.childLastName = in.readString();
        this.listType = in.readInt();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
