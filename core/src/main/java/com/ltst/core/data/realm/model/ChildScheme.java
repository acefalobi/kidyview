package com.ltst.core.data.realm.model;

import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.response.ChildResponse;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ChildScheme extends RealmObject {

    @PrimaryKey
    private int id;

    private boolean sinkWithServer;

    private long serverId;

    private long groupId;

    private String firstName;

    private String lastName;

    private String avatarUrl;

    private String gender;

    private String birthDay;

    private String bloodGroup;

    private String genotype;

    private String allergies;

    private String additional;

    private RealmList<MemberScheme> familyMembers;

    private RealmList<MemberScheme> inviteMembers;

    private RealmList<RealmLong> groupIds;

    private RealmList<RealmLong> fullAccessMembers;

    public ChildScheme(int id) {
        this.id = id;
        this.sinkWithServer = false;
    }

    public ChildScheme() {
    }

    public static ChildScheme putFromRest(Realm realm, ChildResponse rest, int freeChildId) {
        ChildScheme scheme = realm.where(ChildScheme.class)
                .equalTo("serverId", rest.getServerId())
                .findFirst();
        if (scheme == null) {
            scheme = realm.createObject(ChildScheme.class);
            scheme.id = freeChildId;
            scheme.serverId = rest.getServerId();
        }
        scheme.setFirstName(rest.getFirstName());
        scheme.setLastName(rest.getLastName());
        scheme.setAvatarUrl(rest.getAvatarUrl());
        scheme.setGender(rest.getGender());
        scheme.setBirthDay(rest.getBirthDay());
        scheme.setBloodGroup(rest.getBloodGroup());
        scheme.setGenotype(rest.getGenotype());
        scheme.setAllergies(rest.getAllergies());
        scheme.setAdditional(rest.getInformation());
        List<Long> groupIds = rest.getGroupIds();
//        if (groupIds != null && !groupIds.isEmpty()) {
//            scheme.setGroupIds(realm, RealmLong.fromLongList(rest.getGroupIds()));
//        }
        return scheme;
    }

    public static ChildScheme insertChild(Realm realm, Child child) {
        ChildScheme scheme = new ChildScheme(child.getId());
        scheme.setFirstName(child.getFirstName());
        scheme.setLastName(child.getLastName());
        scheme.setAvatarUrl(child.getAvatarUrl());
        scheme.setGender(child.getGender());
        scheme.setBirthDay(child.getBirthDay());
        scheme.setBloodGroup(child.getBloodGroup());
        scheme.setGenotype(child.getGenotype());
        scheme.setAllergies(child.getAllergies());
        scheme.setAdditional(child.getAdditional());
        RealmList<RealmLong> childGroupIds = RealmLong.fromLongList(child.getGroupIds());
        realm.copyToRealmOrUpdate(childGroupIds);
        scheme.setGroupIds(childGroupIds);
        RealmList<RealmLong> fullAccessMembers = RealmLong.fromLongList(child.getFullAccessMembers());
        scheme.setFullAccessMembers(fullAccessMembers);

//        scheme.setGroupIds(realm, RealmLong.fromLongList(child.getGroupIds()));
        realm.insert(scheme);
        return scheme;
    }

    public void updateAll(Child child, Realm realm, boolean afterSyncWithServer) {
        this.setServerId(child.getServerId());
        this.setSinkWithServer(true); // !
        this.setAvatarUrl(child.getAvatarUrl());
        this.setLastName(child.getLastName());
        this.setFirstName(child.getFirstName());
        this.setGender(child.getGender());
        this.setBirthDay(child.getBirthDay());
        this.setBloodGroup(child.getBloodGroup());
        this.setGenotype(child.getGenotype());
        this.setAllergies(child.getAllergies());
        this.setAdditional(child.getAdditional());
        RealmList<RealmLong> fullAccessMembers = RealmLong.fromLongList(child.getFullAccessMembers());
        if (this.getFullAccessMembers() == null) {
            realm.copyToRealmOrUpdate(fullAccessMembers);
            this.setFullAccessMembers(fullAccessMembers);
        } else {
            realm.copyToRealmOrUpdate(fullAccessMembers);
            this.getFullAccessMembers().clear();
            this.getFullAccessMembers().addAll(fullAccessMembers);
        }
        RealmList<RealmLong> groupIds = RealmLong.fromLongList(child.getGroupIds());
        if (this.getGroupIds() == null) {
            realm.copyToRealmOrUpdate(groupIds);
            this.setGroupIds(groupIds);
        } else {
            realm.copyToRealmOrUpdate(groupIds);
            this.getGroupIds().clear();
            this.getGroupIds().addAll(groupIds);
        }
        List<Member> family = child.getFamily();
        if (family != null && !family.isEmpty()) {
            RealmList<MemberScheme> innerFamilySchemes = MemberScheme.fromMembers(family, afterSyncWithServer);
            if (this.getFamilyMembers() == null) {
                realm.copyToRealmOrUpdate(innerFamilySchemes);
                this.setFamilyMembers(innerFamilySchemes);
            } else {
                realm.copyToRealmOrUpdate(innerFamilySchemes);
                this.getFamilyMembers().clear();
                this.getFamilyMembers().addAll(innerFamilySchemes);
            }
        } else {
            if (this.getFamilyMembers() != null) {
                this.getFamilyMembers().clear();
            }
        }
        List<Member> invites = child.getInvites();
        if (invites != null && !invites.isEmpty()) {
            RealmList<MemberScheme> innerInvitesSchemes = MemberScheme.fromMembers(invites, afterSyncWithServer);
            if (this.getInviteMembers() == null) {
                realm.copyToRealmOrUpdate(innerInvitesSchemes);
                this.setInviteMembers(innerInvitesSchemes);
            } else {
                realm.copyToRealmOrUpdate(innerInvitesSchemes);
                this.getInviteMembers().clear();
                this.getInviteMembers()
                        .addAll(innerInvitesSchemes);
            }
        } else {
            if (this.getInviteMembers() != null) {
                this.getInviteMembers().clear();
            }
        }
    }

    public void setFamilyMembers(RealmList<MemberScheme> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public RealmList<MemberScheme> getInviteMembers() {
        return inviteMembers;
    }

    public void setInviteMembers(RealmList<MemberScheme> inviteMembers) {
        this.inviteMembers = inviteMembers;
    }

    public RealmList<MemberScheme> getFamilyMembers() {
        return familyMembers;
    }

    public boolean isSinkWithServer() {
        return sinkWithServer;
    }

    public void setSinkWithServer(boolean sinkWithServer) {
        this.sinkWithServer = sinkWithServer;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getFirstName() {
        return firstName;
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

    public String getAdditional() {
        return additional;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
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

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public RealmList<RealmLong> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(RealmList<RealmLong> groupIds) {
        this.groupIds = groupIds;
    }

    public RealmList<RealmLong> getFullAccessMembers() {
        return fullAccessMembers;
    }

    public void setFullAccessMembers(RealmList<RealmLong> fullAccessMembers) {
        this.fullAccessMembers = fullAccessMembers;
    }
}
