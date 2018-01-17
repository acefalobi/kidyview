package com.ltst.schoolapp.parent.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;

import java.util.ArrayList;
import java.util.List;

public class ChildInGroupInSchool implements Parcelable {
    private Child child;

    private int schoolId;
    private String schoolTitle;

    private long groupId;
    private String groupTitle;
    private String avatarUrl;

    boolean isSelected;

    public Child getChild() {
        return child;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public String getSchoolTitle() {
        return schoolTitle;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setChild(Child child) {
        this.child = child;
    }


    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolTitle(String schoolTitle) {
        this.schoolTitle = schoolTitle;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static List<ChildInGroupInSchool> fromParentChildren(List<ParentChild> parentChildren) {
        List<ChildInGroupInSchool> result = new ArrayList<>();
        for (ParentChild parentChild : parentChildren) {
            Child child = parentChild.getChild();
            List<Long> groupsOfChild = child.getGroupIds();
            for (Long aLong : groupsOfChild) {
                ChildInGroupInSchool object = new ChildInGroupInSchool();
                object.child = child;
                object.schoolId = parentChild.getSchoolId();
                object.schoolTitle = parentChild.getSchoolTitle();

                result.add(object);
            }
        }
        return result;
    }

    public ChildInGroupInSchool() {
    }

    public static String getObjectTitle(ChildInGroupInSchool object) {
        StringBuilder builder = new StringBuilder();
        Child child = object.getChild();
        builder.append(child.getFirstName())
                .append(StringUtils.SPACE)
                .append(StringUtils.IN)
                .append(StringUtils.SPACE)
                .append(object.getGroupTitle())
                .append(StringUtils.COMMA)
                .append(StringUtils.SPACE)
                .append(object.getSchoolTitle());
        return builder.toString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.child, flags);
        dest.writeInt(this.schoolId);
        dest.writeString(this.schoolTitle);
        dest.writeLong(this.groupId);
        dest.writeString(this.groupTitle);
        dest.writeString(this.avatarUrl);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected ChildInGroupInSchool(Parcel in) {
        this.child = in.readParcelable(Child.class.getClassLoader());
        this.schoolId = in.readInt();
        this.schoolTitle = in.readString();
        this.groupId = in.readLong();
        this.groupTitle = in.readString();
        this.avatarUrl = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<ChildInGroupInSchool> CREATOR = new Creator<ChildInGroupInSchool>() {
        @Override public ChildInGroupInSchool createFromParcel(Parcel source) {
            return new ChildInGroupInSchool(source);
        }

        @Override public ChildInGroupInSchool[] newArray(int size) {
            return new ChildInGroupInSchool[size];
        }
    };

    @Override public boolean equals(Object o) {
        if (o instanceof ChildInGroupInSchool) {
            ChildInGroupInSchool object = (ChildInGroupInSchool) o;
            if (child.getServerId() == object.getChild().getServerId()
                    && schoolId == object.getSchoolId()
                    && groupId == object.getGroupId()) {
                return true;
            } else return false;
        } else return false;
    }
}
