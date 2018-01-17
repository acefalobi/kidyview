package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import com.ltst.core.data.realm.model.ChildScheme;
import com.ltst.core.data.realm.model.MemberScheme;
import com.ltst.core.data.realm.model.RealmLong;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.response.GroupResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.RealmList;

public class Child implements Parcelable {

    public static final String BIRTHDAY_FORMAT = "dd/MM/yyyy";
    public static final String SERVER_FORMAT = "yyyy-MM-dd";
    public static final String DISPLAYED_BIRTHDAY_FORMAT = "dd.MM.yyyy";

    public static final String MALE = "male";
    public static final String FEMALE = "female";

    public int getId() {
        return this.id;
    }

    public void updateAll(Child child) {
        this.id = child.id;
        this.serverId = child.serverId;
        this.name = child.name;
        this.lastName = child.lastName;
        this.avatarUrl = child.avatarUrl;
        this.gender = child.gender;
        this.birthDay = child.birthDay;
        this.bloodGroup = child.bloodGroup;
        this.genotype = child.genotype;
        this.allergies = child.allergies;
        this.additional = child.additional;
        this.family = child.family;
        this.invites = child.invites;
        this.groupIds = child.groupIds;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MALE, FEMALE})
    public @interface GENDER {
    }

    private int id;

    private long serverId;

    private String name;
    private String lastName;
    private String avatarUrl;
    private String gender;
    private String birthDay;

    private String bloodGroup;
    private String genotype;
    private String allergies;
    private String additional;

    private List<Member> family;
    private List<Member> invites;

    private List<Long> groupIds;
    private List<Long> fullAccessMembers;
    private List<Group> groups;

    public List<Member> getFamily() {
        if (family == null) {
            return Collections.emptyList();
        }
        return family;
    }

    public void setFamilyMembers(List<Member> members) {
        this.family = members;
    }

    public List<Member> getInvites() {
        if (invites == null) {
            return new ArrayList<>();
        }
        return invites;
    }

    public List<Member> getAllMembers() {
        List<Member> family = getFamily();
        List<Member> invites = getInvites();
        List<Member> allMembers = new ArrayList<>(family.size() + invites.size());
        allMembers.addAll(family);
        allMembers.addAll(invites);
        return allMembers;
    }

    public void setInviteMembers(List<Member> invites) {
        this.invites = invites;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGender(@GENDER String gender) {
        this.gender = gender;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setGenotype(String genotype) {
        this.genotype = genotype;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getFirstName() {
        return name;
    }

    public String getLastName() {
        return lastName;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void addInvite(Member member) {
        if (invites == null || invites.isEmpty()) {
            invites = new ArrayList<>();
        }
        invites.add(member);
    }

    public List<Long> getFullAccessMembers() {
        return fullAccessMembers;
    }

    public void setFullAccessMembers(List<Long> fullAccessMembers) {
        this.fullAccessMembers = fullAccessMembers;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<Long> getGroupIds() {
        if (groupIds == null) {
            return Collections.emptyList();
        }
        return groupIds;
    }

    @Override
    public String toString() {

        return getFirstName() + getLastName();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Child child = (Child) o;

        if (id != child.id) return false;
        if (serverId != child.serverId) return false;
        if (name != null ? !name.equals(child.name) : child.name != null) return false;
        if (lastName != null ? !lastName.equals(child.lastName) : child.lastName != null)
            return false;
        if (avatarUrl != null ? !avatarUrl.equals(child.avatarUrl) : child.avatarUrl != null)
            return false;
        if (gender != null ? !gender.equals(child.gender) : child.gender != null) return false;
        if (birthDay != null ? !birthDay.equals(child.birthDay) : child.birthDay != null)
            return false;
        if (bloodGroup != null ? !bloodGroup.equals(child.bloodGroup) : child.bloodGroup != null)
            return false;
        if (genotype != null ? !genotype.equals(child.genotype) : child.genotype != null)
            return false;
        if (allergies != null ? !allergies.equals(child.allergies) : child.allergies != null)
            return false;
        if (additional != null ? !additional.equals(child.additional) : child.additional != null)
            return false;
        if (family != null ? !family.equals(child.family) : child.family != null) return false;
        if (invites != null ? !invites.equals(child.invites) : child.invites != null) return false;
        if (groupIds != null ? !groupIds.equals(child.groupIds) : child.groupIds != null)
            return false;
        if (fullAccessMembers != null ? !fullAccessMembers.equals(child.fullAccessMembers) : child.fullAccessMembers != null)
            return false;
        boolean checkMembers = checkMemberSizes(child);
        if (!checkMembers) {
            return false;
        }
        return true;
    }

    @Override public int hashCode() {
        int result = id;
        result = 31 * result + (int) (serverId ^ (serverId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (birthDay != null ? birthDay.hashCode() : 0);
        result = 31 * result + (bloodGroup != null ? bloodGroup.hashCode() : 0);
        result = 31 * result + (genotype != null ? genotype.hashCode() : 0);
        result = 31 * result + (allergies != null ? allergies.hashCode() : 0);
        result = 31 * result + (additional != null ? additional.hashCode() : 0);
        result = 31 * result + (family != null ? family.hashCode() : 0);
        result = 31 * result + (invites != null ? invites.hashCode() : 0);
        result = 31 * result + (groupIds != null ? groupIds.hashCode() : 0);
        result = 31 * result + (fullAccessMembers != null ? fullAccessMembers.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        return result;
    }

    private boolean checkMemberSizes(Child child) {
        boolean equals = true;
        List<Member> family = getFamily();
        List<Member> invites = getInvites();
        List<Member> childFamily = child.getFamily();
        List<Member> childInvites = child.getInvites();
        if (family == null && childFamily == null) {
            equals = true;
        } else if (family != null && childFamily == null) {
            equals = false;
        } else
            equals = family != null && family.size() == childFamily.size();
        if (invites == null && childFamily == null) {
            equals = true;
        } else if (invites != null && childInvites == null) {
            equals = false;
        } else {
            equals = invites != null && invites.size() == childInvites.size();
        }
        return equals;
    }

    public static Child fromResponse(ChildResponse childResponse) {

        Child child = new Child();
        child.setName(childResponse.getFirstName());
        child.setLastName(childResponse.getLastName());
        child.setServerId(childResponse.getServerId());
        child.setAvatarUrl(childResponse.getAvatarUrl());
        String gender = childResponse.getGender();
        if (gender != null) {
            child.setGender(gender.equals(Child.FEMALE) ? Child.FEMALE : Child.MALE);
        }
        child.setBirthDay(childResponse.getBirthDay());
        child.setBloodGroup(childResponse.getBloodGroup());
        child.setGenotype(childResponse.getGenotype());
        child.setAdditional(childResponse.getInformation());
        child.setAllergies(childResponse.getAllergies());
        List<ChildResponse.Position> family = childResponse.getFamily();
        if (family != null && !family.isEmpty()) {
            List<Member> members = Member.fromPosition(family);
            child.setFamilyMembers(members);
        }
        List<ChildResponse.Position> invites = childResponse.getInvites();
        if (invites != null && !invites.isEmpty()) {
            List<Member> inviteMembers = Member.fromPosition(invites);
            child.setInviteMembers(inviteMembers);
        }
        List<Long> groupIdsResponse = childResponse.getGroupIds();
        if (groupIdsResponse != null && !groupIdsResponse.isEmpty()) {
            child.setGroupIds(groupIdsResponse);
        }
        List<GroupResponse> groups = childResponse.getGroups();
        if (groups != null) {
            child.setGroups(Group.fromGroupsResposne(groups));
        }
        List<Long> fullAssessMembers = new ArrayList<>();
        if (child.getFamily() != null) {
            for (Member member : child.getFamily()) {
                if (member.getAccessLevel().equals(Member.FULL_ACCESS)) {
                    fullAssessMembers.add(member.getId());
                }
            }
        }
        if (child.getInvites() != null) {
            for (Member member : child.getInvites()) {
                if (member.getAccessLevel().equals(Member.FULL_ACCESS)) {
                    fullAssessMembers.add(member.getId());
                }
            }
        }
        child.setFullAccessMembers(fullAssessMembers);
        return child;
    }

    public static List<Child> fromResponse(List<ChildResponse> childResponses) {
        List<Child> children = new ArrayList<>(childResponses.size());
        for (ChildResponse response : childResponses) {
            children.add(fromResponse(response));
        }
        return children;
    }

    public static Child fromChildScheme(ChildScheme childScheme) {
        Child child = new Child();
        child.setId(childScheme.getId());
        child.setServerId(childScheme.getServerId());
        child.setName(childScheme.getFirstName());
        child.setLastName(childScheme.getLastName());
        child.setAvatarUrl(childScheme.getAvatarUrl());
        String gender = childScheme.getGender();
        if (gender != null) {
            child.setGender(gender.equals(Child.FEMALE) ? Child.FEMALE : Child.MALE);
        }
        child.setBirthDay(childScheme.getBirthDay());
        child.setGenotype(childScheme.getGenotype());
        child.setAdditional(childScheme.getAdditional());
        child.setAllergies(childScheme.getAllergies());
        child.setBloodGroup(childScheme.getBloodGroup());
        List<MemberScheme> family = childScheme.getFamilyMembers();
        if (family != null && !family.isEmpty()) {
            List<Member> members = new ArrayList<>(family.size());
            for (MemberScheme memberScheme : family) {
                members.add(fromScheme(memberScheme));
            }
            child.setFamilyMembers(members);
        }
        List<MemberScheme> invites = childScheme.getInviteMembers();
        if (invites != null && !invites.isEmpty()) {
            List<Member> members = new ArrayList<>(invites.size());
            for (MemberScheme memberScheme : invites) {
                members.add(fromScheme(memberScheme));
            }
            child.setInviteMembers(members);
        }
        RealmList<RealmLong> realmGroupIds = childScheme.getGroupIds();
        if (realmGroupIds != null) {
            List<Long> groupIds = new ArrayList<>(realmGroupIds.size());
            for (RealmLong realmLong : realmGroupIds) {
                groupIds.add(realmLong.getValue());
            }
            child.setGroupIds(groupIds);
        }

        return child;
    }

    public static Member fromScheme(MemberScheme scheme) {
        Member member = new Member(scheme.getMemberId());
        member.setPosition(scheme.getPosition());
        member.setFirstName(scheme.getFirstName());
        member.setLastName(scheme.getLastName());
        member.setEmail(scheme.getEmail());
        member.setPhone(scheme.getPhone());
        member.setAvatarUrl(scheme.getAvatarUrl());
        member.setAccessLevel(scheme.getAccessLevel());
        member.setLayerIdentity(scheme.getLayerIdentity());
        return member;
    }

    public Child() {
    }

    public Child clone() {
        Child child = new Child();
        child.setId(id);
        child.setServerId(serverId);
        child.setName(name);
        child.setLastName(lastName);
        child.setAvatarUrl(avatarUrl);
        child.setGender(gender);
        child.setBirthDay(birthDay);
        child.setBloodGroup(bloodGroup);
        child.setGenotype(genotype);
        child.setAllergies(allergies);
        child.setAdditional(additional);
        child.setFamilyMembers(family);
        child.setInviteMembers(invites);
        child.setGroupIds(groupIds);
        return child;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.serverId);
        dest.writeString(this.name);
        dest.writeString(this.lastName);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.gender);
        dest.writeString(this.birthDay);
        dest.writeString(this.bloodGroup);
        dest.writeString(this.genotype);
        dest.writeString(this.allergies);
        dest.writeString(this.additional);
        dest.writeTypedList(this.family);
        dest.writeTypedList(this.invites);
        dest.writeList(this.groupIds);
        dest.writeList(this.groups);
    }

    protected Child(Parcel in) {
        this.id = in.readInt();
        this.serverId = in.readLong();
        this.name = in.readString();
        this.lastName = in.readString();
        this.avatarUrl = in.readString();
        this.gender = in.readString();
        this.birthDay = in.readString();
        this.bloodGroup = in.readString();
        this.genotype = in.readString();
        this.allergies = in.readString();
        this.additional = in.readString();
        this.family = in.createTypedArrayList(Member.CREATOR);
        this.invites = in.createTypedArrayList(Member.CREATOR);
        this.groupIds = new ArrayList<Long>();
        in.readList(this.groupIds, Long.class.getClassLoader());
        this.groups = new ArrayList<Group>();
        in.readList(this.groups, Group.class.getClassLoader());
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override public Child createFromParcel(Parcel source) {
            return new Child(source);
        }

        @Override public Child[] newArray(int size) {
            return new Child[size];
        }
    };
}
