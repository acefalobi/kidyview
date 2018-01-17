package com.ltst.core.data.realm.model;

import com.ltst.core.data.model.Member;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class MemberScheme extends RealmObject {

    @PrimaryKey
    private String email;
    private long memberId;
    private long familyId;
    private boolean sync;
    private String position;
    private String firstName;
    private String lastName;
    private String phone;
    private String additionalPhone;
    private String avatarUrl;
    private String accessLevel;
    private boolean cached;
    private String layerIdentity;

    public MemberScheme() {
    }

    public MemberScheme(long memberId) {
        this.memberId = memberId;
    }

    public long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(long familyId) {
        this.familyId = familyId;
    }

    public long getMemberId() {
        return memberId;
    }

    public String getPosition() {
        return position;
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

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public String getLayerIdentity() {
        return layerIdentity;
    }

    public void setLayerIdentity(String layerIdentity) {
        this.layerIdentity = layerIdentity;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public boolean isCached() {
        return cached;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public static RealmList<MemberScheme> fromMembers(List<Member> members, boolean afterSincWithServer) {
        RealmList<MemberScheme> schemes = new RealmList<MemberScheme>();
        for (Member member : members) {
            schemes.add(fromFamilyMember(member, afterSincWithServer));
        }
        return schemes;
    }

    public static MemberScheme fromFamilyMember(Member member, boolean afterSyncWithServer) {
        MemberScheme scheme = new MemberScheme(member.getId());
        scheme.setPosition(member.getPosition());
        if (afterSyncWithServer) {
            scheme.setSync(true);
        }
        scheme.setFirstName(member.getFirstName());
        scheme.setLastName(member.getLastName());
        scheme.setEmail(member.getEmail());
        scheme.setPhone(member.getPhone());
        scheme.setAvatarUrl(member.getAvatarUrl());
        scheme.setAdditionalPhone(member.getSecondPhone());
        scheme.setAccessLevel(member.getAccessLevel());
        scheme.setFamilyId(member.getFamilyId());
        scheme.setLayerIdentity(member.getLayerIdentity());
        return scheme;
    }
}
