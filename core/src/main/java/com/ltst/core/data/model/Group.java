package com.ltst.core.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.realm.model.GroupScheme;
import com.ltst.core.data.response.GroupResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 14.09.2016.
 */
public class Group implements Parcelable {
    private long id;
    private String title;
    private String avatarUrl;
    private boolean selected;

    private Group(long id, String title, String avatarUrl) {
        this.title = title;
        this.avatarUrl = avatarUrl;
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public long getId() {
        return id;
    }

    public static Group fromGroupResponse(GroupResponse groupResponse) {
        return new Group(groupResponse.serverId, groupResponse.title, groupResponse.avatarUrl);
    }

    public static List<Group> fromGroupsResposne(List<GroupResponse> responses) {
        List<Group> groups = new ArrayList<>(responses.size());
        for (GroupResponse response : responses) {
            groups.add(new Group(response.serverId, response.title, response.avatarUrl));
        }
        return groups;
    }

    public static Group fromScheme(GroupScheme scheme) {
        if (scheme == null) {
            return Group.fakeGroup();
        }
        Group group = new Group(scheme.getId(), scheme.getTitle(), scheme.getAvatarUrl());
        group.setSelected(scheme.isSelected());
        return group;
    }

    public static List<Group> fromSchemes(List<GroupScheme> schemes) {
        List<Group> result = new ArrayList<>(schemes.size());
        for (GroupScheme scheme : schemes) {
            result.add(Group.fromScheme(scheme));
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (id != group.id) return false;
        if (selected != group.selected) return false;
        if (!title.equals(group.title)) return false;
        return avatarUrl != null ? avatarUrl.equals(group.avatarUrl) : group.avatarUrl == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        return result;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.avatarUrl);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected Group(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.avatarUrl = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static Group fakeGroup() {
        return new Group(0, "", "");
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
