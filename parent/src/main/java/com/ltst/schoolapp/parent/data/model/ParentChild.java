package com.ltst.schoolapp.parent.data.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.util.DiffUtil;

import com.ltst.core.data.model.Child;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.response.ParentChildResponse;

import java.util.List;

public class ParentChild implements Parcelable {
    private int schoolId;

    private String schoolTitle;

    private String schoolAvatar;

    private Child child;

    public int getSchoolId() {
        return schoolId;
    }

    public String getSchoolTitle() {
        return schoolTitle;
    }

    public String getSchoolAvatar() {
        return schoolAvatar;
    }

    public Child getChild() {
        return child;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolTitle(String schoolTitle) {
        this.schoolTitle = schoolTitle;
    }

    public void setSchoolAvatar(String schoolAvatar) {
        this.schoolAvatar = schoolAvatar;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public static ParentChild fromResponse(ParentChildResponse response) {
        ParentChild parentChild = new ParentChild();
        parentChild.setSchoolId(response.getSchoolId());
        parentChild.setSchoolTitle(response.getSchoolTitle());
        parentChild.setSchoolAvatar(response.getSchoolAvatar());
        ChildResponse responseChild = response.getChild();
        Child child = Child.fromResponse(responseChild);
        parentChild.setChild(child);
        return parentChild;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.schoolId);
        dest.writeString(this.schoolTitle);
        dest.writeString(this.schoolAvatar);
        dest.writeParcelable(this.child, flags);
    }

    public ParentChild() {
    }

    protected ParentChild(Parcel in) {
        this.schoolId = in.readInt();
        this.schoolTitle = in.readString();
        this.schoolAvatar = in.readString();
        this.child = in.readParcelable(Child.class.getClassLoader());
    }

    public static final Parcelable.Creator<ParentChild> CREATOR = new Parcelable.Creator<ParentChild>() {
        @Override public ParentChild createFromParcel(Parcel source) {
            return new ParentChild(source);
        }

        @Override public ParentChild[] newArray(int size) {
            return new ParentChild[size];
        }
    };

    @Override public boolean equals(Object o) {
        if (o instanceof ParentChild) {
            ParentChild object = (ParentChild) o;
            boolean result = true;
            if (!object.schoolTitle.equals(this.schoolTitle)) {
                result = false;
            }
            if (object.schoolId != this.schoolId) {
                result = false;
            }
            if (!object.child.equals(this.child)) {
                result = false;
            }
            return result;

        } else {
            return false;
        }
    }

    public ParentChild clone (){
        ParentChild parentChild = new ParentChild();
        parentChild.schoolId = schoolId;
        parentChild.schoolTitle = schoolTitle;
        parentChild.schoolAvatar = schoolAvatar;
        parentChild.child = child.clone();
        return parentChild;
    }

    public static class DiffCallBack extends DiffUtil.Callback {

        private List<ParentChild> oldList;
        private List<ParentChild> newList;

        public DiffCallBack(List<ParentChild> oldList, List<ParentChild> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override public int getOldListSize() {
            return oldList.size();
        }

        @Override public int getNewListSize() {
            return newList.size();
        }

        @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            final ParentChild oldItem = oldList.get(oldItemPosition);
            final ParentChild newItem = newList.get(newItemPosition);
            return oldItem.getSchoolId() == newItem.getSchoolId() &&
                    oldItem.getChild().getServerId() == newItem.getChild().getServerId();
        }

        @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
