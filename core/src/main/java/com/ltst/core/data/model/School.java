package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.realm.model.SchoolScheme;
import com.ltst.core.data.response.SchoolResponse;

import java.util.ArrayList;
import java.util.List;

public class School implements Parcelable {
    private String address;
    private String title;
    private String phone;
    private String additionalPhone;
    private String email;

    public String getAddress() {
        return address;
    }

    public String getTitle() {
        return title;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public static School fromSchoolScheme(SchoolScheme schoolScheme) {
        if (schoolScheme != null) {
            School school = new School();
            school.address = schoolScheme.getAddress();
            school.title = schoolScheme.getTitle();
            school.phone = schoolScheme.getPhone();
            school.email = schoolScheme.getEmail();
            school.additionalPhone = schoolScheme.getAdditionalPhone();
            return school;
        } else {
            return null;
        }

    }

    public static List<School> fromResponse(List<SchoolResponse> responses) {
        List<School> result = new ArrayList<>(responses.size());
        for (SchoolResponse response : responses) {
            School school = new School();
            school.address = response.getAddress();
            school.title = response.title;
            school.phone = response.phone;
            school.email = response.email;
            result.add(school);
        }
        return result;
    }


    public School() {
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        School school = (School) o;

        if (address != null ? !address.equals(school.address) : school.address != null)
            return false;
        if (title != null ? !title.equals(school.title) : school.title != null) return false;
        if (phone != null ? !phone.equals(school.phone) : school.phone != null) return false;
        if (additionalPhone != null ? !additionalPhone.equals(school.additionalPhone) : school.additionalPhone != null)
            return false;
        return email != null ? email.equals(school.email) : school.email == null;

    }

    @Override public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (additionalPhone != null ? additionalPhone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.title);
        dest.writeString(this.phone);
        dest.writeString(this.additionalPhone);
        dest.writeString(this.email);
    }

    protected School(Parcel in) {
        this.address = in.readString();
        this.title = in.readString();
        this.phone = in.readString();
        this.additionalPhone = in.readString();
        this.email = in.readString();
    }

    public static final Creator<School> CREATOR = new Creator<School>() {
        @Override public School createFromParcel(Parcel source) {
            return new School(source);
        }

        @Override public School[] newArray(int size) {
            return new School[size];
        }
    };
}
