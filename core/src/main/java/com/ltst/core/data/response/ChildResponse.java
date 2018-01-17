package com.ltst.core.data.response;

import com.squareup.moshi.Json;

import java.util.List;

public class ChildResponse {

    @Json(name = "id")
    private long serverId;

    @Json(name = "group_id")
    private int groupId;

    @Json(name = "first_name")
    private String firstName;

    @Json(name = "last_name")
    private String lastName;

    @Json(name = "avatar_url")
    private String avatarUrl;

    @Json(name = "gender")
    private String gender;

    @Json(name = "date_of_birth")
    private String birthDay;

    @Json(name = "blood_group")
    private String bloodGroup;

    @Json(name = "genotype")
    private String genotype;

    @Json(name = "allergies")
    private String allergies;

    @Json(name = "information")
    private String information;

    @Json(name = "family")
    private List<Position> family;

    @Json(name = "invites")
    private List<Position> invites;

    @Json(name = "group_ids") // used in teacher app
    private List<Long> groupIds;

    @Json(name = "groups")
    private List<GroupResponse> groups; // used in parent app

    public static class Position {

        @Json(name = "id")
        long familyId;

        @Json(name = "position")
        String position;

        @Json(name = "access_level")
        String accessLevel;

        @Json(name = "family_member")
        FamilyMemberResponse familyMemberResponse;

        public String getPosition() {
            return position;
        }

        public FamilyMemberResponse getFamilyMemberResponse() {
            return familyMemberResponse;
        }

        public String getAccessLevel() {
            return accessLevel;
        }

        public long getFamilyId() {
            return familyId;
        }
    }

    public static class FamilyMemberResponse {

        @Json(name = "id")
        long memberId;

        @Json(name = "first_name")
        String firstName;

        @Json(name = "last_name")
        String lastName;

        @Json(name = "email")
        String email;

        @Json(name = "phone")
        String phone;

        @Json(name = "avatar_url")
        String avatarUrl;

        @Json(name = "layer_identity")
        String layerIdentity;

        public long getMemberId() {
            return memberId;
        }

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

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getLayerIdentity() {
            return layerIdentity;
        }
    }

    public List<Position> getInvites() {
        return invites;
    }

    public List<Position> getFamily() {
        return family;
    }

    public long getServerId() {
        return serverId;
    }

    public int getGroupId() {
        return groupId;
    }

    public List<GroupResponse> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupResponse> groups) {
        this.groups = groups;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getGenotype() {
        return genotype;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getInformation() {
        return information;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }
}
