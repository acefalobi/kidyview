package com.ltst.core.data.request;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.model.School;
import com.squareup.moshi.Json;

public class ProfileUpdateRequest {

    @Json(name = "first_name")
    public String firstName;

    @Json(name = "last_name")
    public String lastName;

    @Json(name = "email")
    private String email;

    @Json(name = "phone")
    public String phone;

    @Json(name = "additional_phone")
    public String additionalPhone;

    @Json(name = "school")
    public SchoolRequest school;


    public static final class SchoolRequest {
        @Json(name = "address")
        public String address;

        @Json(name = "title")
        public String title;

        @Json(name = "phone")
        public String phone;

        @Json(name = "email")
        public String email;

        @Json(name = "additional_phone")
        public String additionalPhone;

        public static SchoolRequest fromSchool(School school) {
            SchoolRequest request = new SchoolRequest();
            request.title = school.getTitle();
            request.address = school.getAddress();
            request.email = school.getEmail();
            request.phone = school.getPhone();
            request.additionalPhone = StringUtils.isBlank(school.getAdditionalPhone())
                    ? StringUtils.EMPTY
                    : school.getAdditionalPhone();
            return request;
        }
    }

    public static ProfileUpdateRequest fromProfile(Profile profile) {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.firstName = profile.getFirstName();
        request.lastName = profile.getLastName();
        request.email = profile.getEmail();
        request.phone = profile.getPhone();
        request.additionalPhone = profile.getAdditionalPhone();
        SchoolRequest schoolRequest = SchoolRequest.fromSchool(profile.getSchool());
        if (schoolRequest != null) {
            request.school = schoolRequest;
        }
        return request;
    }


}
