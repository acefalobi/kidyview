package com.ltst.schoolapp.parent.ui.checkout.fragment.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.model.Profile;
import com.ltst.schoolapp.parent.data.model.ParentChild;

import java.util.ArrayList;
import java.util.List;

public class ParentProfile implements Parcelable {
    Profile profile;
    List<ParentChild> childList;

    public ParentProfile(Profile profile, List<ParentChild> childList) {
        this.profile = profile;
        this.childList = childList;
    }


    public Profile getProfile() {
        return profile;
    }

    public List<ParentChild> getChildList() {
        return childList;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.profile, flags);
        dest.writeList(this.childList);
    }

    protected ParentProfile(Parcel in) {
        this.profile = in.readParcelable(Profile.class.getClassLoader());
        this.childList = new ArrayList<ParentChild>();
        in.readList(this.childList, ParentChild.class.getClassLoader());
    }

    public static final Creator<ParentProfile> CREATOR = new Creator<ParentProfile>() {
        @Override public ParentProfile createFromParcel(Parcel source) {
            return new ParentProfile(source);
        }

        @Override public ParentProfile[] newArray(int size) {
            return new ParentProfile[size];
        }
    };

    public ParentProfile clone() {
        Profile clone = profile.clone();
        List<ParentChild> parentChildren = null;
        if (childList != null) {
            parentChildren = new ArrayList<>(childList.size());
            for (ParentChild parentChild : childList) {
                parentChildren.add(parentChild.clone());
            }
        }
        return new ParentProfile(clone, parentChildren);

    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParentProfile that = (ParentProfile) o;

        if (profile != null ? !profile.equals(that.profile) : that.profile != null) return false;
        return childList != null ? childList.equals(that.childList) : that.childList == null;

    }

    @Override public int hashCode() {
        int result = profile != null ? profile.hashCode() : 0;
        result = 31 * result + (childList != null ? childList.hashCode() : 0);
        return result;
    }
}
