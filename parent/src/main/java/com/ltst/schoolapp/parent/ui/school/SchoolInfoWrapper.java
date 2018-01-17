package com.ltst.schoolapp.parent.ui.school;


import android.os.Parcel;
import android.os.Parcelable;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.schoolapp.parent.data.model.SchoolInfo;

import java.util.List;

public class SchoolInfoWrapper implements Parcelable {

    public String avatarUrl;
    public String title;
    public String address;
    public String phoneNumber;
    public String additionalPhoneNumber;
    public String email;
    public List<String> names;
    public boolean isTeacher;

    public static SchoolInfoWrapper fromTeacher(SchoolInfo.Teacher teacher) {
        SchoolInfoWrapper wrapper = new SchoolInfoWrapper();
        wrapper.isTeacher = true;
        wrapper.avatarUrl = teacher.getAvatarUrl();
        wrapper.title = teacher.getFirstName() + StringUtils.SPACE + teacher.getLastName();
        wrapper.phoneNumber = teacher.getPhoneNumber();
        wrapper.additionalPhoneNumber = teacher.getAdditionalPhoneNumber() != null
                ? teacher.getAdditionalPhoneNumber()
                : StringUtils.EMPTY;
        wrapper.email = teacher.getEmail();
        return wrapper;
    }

    public static SchoolInfoWrapper fromSchool(SchoolInfo.InfoSchool school) {
        SchoolInfoWrapper schoolWrapper = new SchoolInfoWrapper();
        schoolWrapper.title = school.getTitle();
        schoolWrapper.avatarUrl = school.getAvatarUrl();
        schoolWrapper.address = school.getAddress();
        schoolWrapper.phoneNumber = school.getPhoneNumber();
        schoolWrapper.additionalPhoneNumber = school.getAdditionalPhoneNumber() != null
                ? school.getAdditionalPhoneNumber()
                : StringUtils.EMPTY;
        schoolWrapper.email = school.getEmail();
        return schoolWrapper;
    }

    public SchoolInfoWrapper() {
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatarUrl);
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.additionalPhoneNumber);
        dest.writeString(this.email);
        dest.writeStringList(this.names);
        dest.writeByte(this.isTeacher ? (byte) 1 : (byte) 0);
    }

    protected SchoolInfoWrapper(Parcel in) {
        this.avatarUrl = in.readString();
        this.title = in.readString();
        this.address = in.readString();
        this.phoneNumber = in.readString();
        this.additionalPhoneNumber = in.readString();
        this.email = in.readString();
        this.names = in.createStringArrayList();
        this.isTeacher = in.readByte() != 0;
    }

    public static final Creator<SchoolInfoWrapper> CREATOR = new Creator<SchoolInfoWrapper>() {
        @Override public SchoolInfoWrapper createFromParcel(Parcel source) {
            return new SchoolInfoWrapper(source);
        }

        @Override public SchoolInfoWrapper[] newArray(int size) {
            return new SchoolInfoWrapper[size];
        }
    };
}
