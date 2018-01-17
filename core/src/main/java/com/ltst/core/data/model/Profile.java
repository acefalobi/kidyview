package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.realm.model.ProfileScheme;
import com.ltst.core.data.response.ProfileResponse;
import com.ltst.core.data.response.SchoolResponse;
import com.ltst.core.util.validator.ValidateType;

import java.util.Map;

public class Profile implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String additionalPhone;
    private String avatarUrl;

    private School school;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public School getSchool() {
        return school;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public static Profile fromResponse(ProfileResponse profileResponse) {
        Profile profile = new Profile();
        profile.avatarUrl = profileResponse.getAvatarUrl();
        profile.firstName = profileResponse.getFirstName();
        profile.lastName = profileResponse.getLastName();
        profile.phone = profileResponse.getPhone();
        profile.additionalPhone = profileResponse.getAdditionalPhone();
        profile.email = profileResponse.getEmail();
        SchoolResponse responseSchool = profileResponse.getSchool();
        if (responseSchool != null) {
            School school = new School();
            school.setTitle(responseSchool.title);
            school.setAddress(responseSchool.address);
            school.setPhone(responseSchool.phone);
            school.setEmail(responseSchool.email);
            school.setAdditionalPhone(responseSchool.additionalPhone);
            profile.setSchool(school);
        }
        return profile;
    }

    public static Profile fromScheme(ProfileScheme profileScheme) {
        Profile profile = new Profile();
        profile.avatarUrl = profileScheme.getAvatarUrl();
        profile.email = profileScheme.getEmail();
        profile.firstName = profileScheme.getFirstName();
        profile.lastName = profileScheme.getLastName();
        profile.phone = profileScheme.getPhone();
        profile.additionalPhone = profileScheme.getAdditionalPhone();
        profile.school = School.fromSchoolScheme(profileScheme.getSchool());
        return profile;
    }

    public static Profile fromValidatedFields(Map<ValidateType, String> validatedFields) {
        Profile profile = new Profile();
        profile.setFirstName(validatedFields.get(ValidateType.NAME));
        profile.setLastName(validatedFields.get(ValidateType.LAST_NAME));
        profile.setEmail(validatedFields.get(ValidateType.PERSONAL_EMAIL));
        profile.setPhone(validatedFields.get(ValidateType.PERSONAL_PHONE));
        profile.setAdditionalPhone(validatedFields.get(ValidateType.SECOND_PHONE));
        School school = new School();
        school.setTitle(validatedFields.get(ValidateType.SCHOOL_TITLE));
        school.setAddress(validatedFields.get(ValidateType.SCHOOL_ADDRESS));
        school.setEmail(validatedFields.get(ValidateType.SCHOOL_EMAIL));
        school.setPhone(validatedFields.get(ValidateType.SCHOOL_PHONE));
        school.setAdditionalPhone(validatedFields.get(ValidateType.SCHOOL_ADDITIONAL_PHONE));
        profile.setSchool(school);
        return profile;
    }

    public Profile() {
    }

    public Profile clone() {
        Profile result = new Profile();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setEmail(email);
        result.setPhone(phone);
        result.setAdditionalPhone(additionalPhone);
        result.setAvatarUrl(avatarUrl);
        result.setSchool(school);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (firstName != null ? !firstName.equals(profile.firstName) : profile.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(profile.lastName) : profile.lastName != null)
            return false;
        if (email != null ? !email.equals(profile.email) : profile.email != null) return false;
        if (phone != null ? !phone.equals(profile.phone) : profile.phone != null) return false;
        if (additionalPhone != null ? !additionalPhone.equals(profile.additionalPhone) : profile.additionalPhone != null)
            return false;
        if (avatarUrl != null ? !avatarUrl.equals(profile.avatarUrl) : profile.avatarUrl != null)
            return false;
        return school != null ? school.equals(profile.school) : profile.school == null;

    }

    @Override public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (additionalPhone != null ? additionalPhone.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (school != null ? school.hashCode() : 0);
        return result;
    }

    public String getFullName() {
        return firstName + StringUtils.SPACE + lastName;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.additionalPhone);
        dest.writeString(this.avatarUrl);
        dest.writeParcelable(this.school, flags);
    }

    protected Profile(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.additionalPhone = in.readString();
        this.avatarUrl = in.readString();
        this.school = in.readParcelable(School.class.getClassLoader());
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
