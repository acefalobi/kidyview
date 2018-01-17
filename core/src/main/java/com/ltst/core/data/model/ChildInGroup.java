package com.ltst.core.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.realm.model.ChildInGroupScheme;
import com.ltst.core.data.response.ChildInGroupResponse;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class ChildInGroup implements Parcelable {

    private Long childId;

    private String firtName;

    private String lastName;

    private String childAvatarUrl;

    private Long groupId;

    private String groupTitle;

    private String groupAvatarUrl;

    public ChildInGroup(Long childId, String firtName, String lastName, Long groupId,
                        String groupTitle, String groupAvatarUrl, String childAvatarUrl) {
        this.childId = childId;
        this.firtName = firtName;
        this.lastName = lastName;
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupAvatarUrl = groupAvatarUrl;
        this.childAvatarUrl = childAvatarUrl;
    }

    public Long getChildId() {
        return childId;
    }

    public String getFirtName() {
        return firtName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getChildAvatarUrl() {
        return childAvatarUrl;
    }

    public String getGroupAvatarUrl() {
        return groupAvatarUrl;
    }



    public static List<ChildInGroup> fromResponse(List<ChildInGroupResponse> responses) {
        List<ChildInGroup> result = new ArrayList<>(responses.size());
        for (ChildInGroupResponse response : responses) {
            result.add(new ChildInGroup(response.getChildId(), response.getFirstName(),
                    response.getLastName(), response.getGroupId(), response.getGroupTitle(),
                    response.getGroupAvatarUrl(),response.getChildAvatarUrl()));
        }
        return result;
    }

    public static List<ChildInGroup> fromSchemes(RealmList<ChildInGroupScheme> schemes) {
        List<ChildInGroup> result = new ArrayList<>(schemes.size());
        for (ChildInGroupScheme scheme : schemes) {
            result.add(fromScheme(scheme));
        }
        return result;
    }

    public static ChildInGroup fromScheme(ChildInGroupScheme scheme) {
        return new ChildInGroup(scheme.getChildId(),
                scheme.getFirtName(),
                scheme.getLastName(),
                scheme.getGroupId(),
                scheme.getGroupTitle(),
                scheme.getGroupAvatarUrl(),
                scheme.getChildAvatarUrl());
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.childId);
        dest.writeString(this.firtName);
        dest.writeString(this.lastName);
        dest.writeString(this.childAvatarUrl);
        dest.writeValue(this.groupId);
        dest.writeString(this.groupTitle);
        dest.writeString(this.groupAvatarUrl);
    }

    protected ChildInGroup(Parcel in) {
        this.childId = (Long) in.readValue(Long.class.getClassLoader());
        this.firtName = in.readString();
        this.lastName = in.readString();
        this.childAvatarUrl = in.readString();
        this.groupId = (Long) in.readValue(Long.class.getClassLoader());
        this.groupTitle = in.readString();
        this.groupAvatarUrl = in.readString();
    }

    public static final Parcelable.Creator<ChildInGroup> CREATOR = new Parcelable.Creator<ChildInGroup>() {
        @Override public ChildInGroup createFromParcel(Parcel source) {
            return new ChildInGroup(source);
        }

        @Override public ChildInGroup[] newArray(int size) {
            return new ChildInGroup[size];
        }
    };
}
