package com.ltst.core.data;

import android.support.annotation.NonNull;

import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.ChildInGroup;
import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.realm.model.ChildCheckScheme;
import com.ltst.core.data.realm.model.ChildInGroupScheme;
import com.ltst.core.data.realm.model.ChildScheme;
import com.ltst.core.data.realm.model.ChildStateScheme;
import com.ltst.core.data.realm.model.GroupScheme;
import com.ltst.core.data.realm.model.MemberScheme;
import com.ltst.core.data.realm.model.ProfileScheme;
import com.ltst.core.data.realm.model.RealmLong;
import com.ltst.core.data.realm.model.SchoolScheme;
import com.ltst.core.data.realm.model.TeacherScheme;
import com.ltst.core.data.request.CheckRequest;
import com.ltst.core.data.response.ProfileResponse;
import com.ltst.core.data.response.SchoolResponse;
import com.ltst.core.data.rest.model.RestChildCheck;

import java.lang.ref.SoftReference;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;

public class DataBaseService {

    private SoftReference<Group> groupSoftReference;

    public DataBaseService(Realm realm) {
    }

    public void saveTeacher(TeacherScheme profile) {
        Realm defRealm = Realm.getDefaultInstance();
        defRealm.beginTransaction();
        TeacherScheme teacherScheme = defRealm.createObject(TeacherScheme.class);
        teacherScheme.setFirstName(profile.getFirstName());
        teacherScheme.setLastName(profile.getLastName());
        teacherScheme.setEmail(profile.getEmail());
        teacherScheme.setPhone(profile.getPhone());
        teacherScheme.setSchool(defRealm.copyToRealm(profile.getSchool()));
        defRealm.commitTransaction();
    }

    public Observable<ProfileScheme> createProfile(final ProfileScheme profileScheme) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SchoolScheme school = realm.where(SchoolScheme.class).findFirst();
                if (school == null) {
                    school = realm.createObject(SchoolScheme.class);
                }
                school.setAddress(null);
                school.setPhone(null);
                school.setTitle(null);
                school.setEmail(null);

                ProfileScheme profile = realm.where(ProfileScheme.class).findFirst();
                if (profile == null) {
                    profile = realm.createObject(ProfileScheme.class);
                }
                profile.setEmail(profileScheme.getEmail());
                profile.setSchool(school);
                /*for parent application*/
                profile.setAdditionalPhone(profileScheme.getAdditionalPhone());
                profile.setFirstName(profileScheme.getFirstName());
                profile.setLastName(profileScheme.getLastName());
                profile.setAvatarUrl(profileScheme.getAvatarUrl());
            }
        });
        defaultInstance.close();
        return Observable.just(profileScheme);
    }

    public Observable<Profile> updateProfile(final ProfileResponse profileResponse) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.executeTransaction(realm -> {
            ProfileScheme profile = realm.where(ProfileScheme.class).findFirst();
            if (profile == null) {
                profile = realm.createObject(ProfileScheme.class);
            }
            profile.setEmail(profileResponse.getEmail());
            profile.setPhone(profileResponse.getPhone());
            profile.setAdditionalPhone(profileResponse.getAdditionalPhone());
            profile.setFirstName(profileResponse.getFirstName());
            profile.setLastName(profileResponse.getLastName());
            profile.setAvatarUrl(profileResponse.getAvatarUrl());
            SchoolScheme schoolScheme = profile.getSchool();
            if (schoolScheme == null) {
                schoolScheme = realm.createObject(SchoolScheme.class);
            }
            SchoolResponse school = profileResponse.getSchool();
            if (school != null) {
                schoolScheme.setTitle(school.title);
                schoolScheme.setAddress(school.address);
                schoolScheme.setPhone(school.phone);
                schoolScheme.setEmail(school.email);
                schoolScheme.setAdditionalPhone(school.additionalPhone);
            }
            profile.setSchool(schoolScheme);
        });
        defaultInstance.close();
        return Observable.just(Profile.fromResponse(profileResponse));
    }

    public Observable<Profile> getProfile() {
        Realm realm = Realm.getDefaultInstance();
        ProfileScheme first = realm.where(ProfileScheme.class).findFirst();
        if (first == null) {
            return Observable.just(null);
        }
        Profile profile = Profile.fromScheme(first);
        realm.close();
        return Observable.just(profile);
    }

    public Observable<Child> insertChild(final Child child) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            ChildScheme.insertChild(realm1, child);
        });
        realm.close();
        return Observable.just(child);
    }

    public static int getFreeChildId() {
        Realm realm = Realm.getDefaultInstance();
        int id = getRandomId();
        RealmQuery<ChildScheme> query = realm.where(ChildScheme.class);
        query.equalTo("id", id);
        RealmResults<ChildScheme> results = query.findAll();
        if (!results.isEmpty()) {
            return getFreeChildId();
        } else {
            realm.close();
            return id;
        }
    }

    private static int getRandomId() {
        SecureRandom secureRandom = new SecureRandom();
        int random = secureRandom.nextInt(2048);
        if (random == 0) {
            return getRandomId();
        }
        return random;
    }

    public Observable<Child> updateWithInnerId(final Child child) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<ChildScheme> query = realm.where(ChildScheme.class).equalTo("id", child.getId());
        ChildScheme first = query.findFirst();
        first.updateAll(child, realm, false);
//        List<Member> family = child.getFamily();
//        if (family != null) {
//            for (Member member : family) {
//                MemberScheme familyMemberScheme = MemberScheme.fromFamilyMember(member);
//                realm.copyToRealmOrUpdate(familyMemberScheme);
//                first.getFamilyMembers().add(familyMemberScheme);
//            }
//        }
        realm.commitTransaction();
        realm.close();
        return Observable.just(child);
    }

    public Observable<Group> updateGroups(List<Group> serverGroups) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        long selectedGroupId = 0;

        RealmQuery<GroupScheme> query = realm.where(GroupScheme.class).equalTo("isSelected", true);
        GroupScheme selectedGroup = query.findFirst();
        if (selectedGroup != null) {
            selectedGroupId = selectedGroup.getId();
        }
        realm.where(GroupScheme.class).findAll().deleteAllFromRealm();
        List<GroupScheme> schemes = GroupScheme.fromGroups(serverGroups);
        realm.copyToRealmOrUpdate(schemes);
        selectedGroup = null;
        if (selectedGroupId != 0) {
            for (GroupScheme groupScheme : schemes) {
                if (groupScheme.getId() == selectedGroupId) {
                    groupScheme.setSelected(true);
                    selectedGroup = groupScheme;
                    break;
                }
            }
        }
        if (selectedGroup == null && !schemes.isEmpty()) {
            GroupScheme firstScheme = schemes.get(0);
            firstScheme.setSelected(true);
            selectedGroup = firstScheme;
        }
        Group result = null;
        if (selectedGroup != null) {
            realm.copyToRealmOrUpdate(selectedGroup);
            result = Group.fromScheme(selectedGroup);
        }

        realm.commitTransaction();
        realm.close();
        if (result != null) {
            return Observable.just(result);
        } else return Observable.empty();
    }

    public Observable<List<Group>> groupUpdate(List<Group> groups) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.beginTransaction();
        long selectedGroupId = 0;
        RealmQuery<GroupScheme> query = defaultInstance.where(GroupScheme.class).equalTo("isSelected", true);
        GroupScheme selectedGroup = query.findFirst();
        if (selectedGroup != null) {
            selectedGroupId = selectedGroup.getId();
        }
        defaultInstance.where(GroupScheme.class).findAll().deleteAllFromRealm();
        List<GroupScheme> groupSchemes = GroupScheme.fromGroups(groups);
        if (selectedGroupId != 0) {
            for (GroupScheme groupScheme : groupSchemes) {
                if (groupScheme.getId() == selectedGroupId) {
                    groupScheme.setSelected(true);
                    break;
                }
            }
        }
        defaultInstance.copyToRealmOrUpdate(groupSchemes);
        List<Group> result = Group.fromSchemes(groupSchemes);
        defaultInstance.commitTransaction();
        defaultInstance.close();
        return Observable.just(result);

    }

    public Observable<List<Group>> getCachedGroups() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<GroupScheme> all = realm.where(GroupScheme.class).findAll();
        List<Group> result = Group.fromSchemes(all);
        realm.commitTransaction();
        realm.close();
        return Observable.just(result);
    }


    public Observable<Group> getSelectedGroup() {
        if (groupSoftReference != null) {
            Group group = groupSoftReference.get();
            if (group != null) {
                return Observable.just(group);
            } else {
                return getGroupFromDataBase();
            }
        } else {
            return getGroupFromDataBase();
        }

    }

    public Observable<Group> getCachedGroupById(long groupId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        GroupScheme groupScheme = realm.where(GroupScheme.class).equalTo("id", groupId).findFirst();
        Group result = Group.fromScheme(groupScheme);
        realm.commitTransaction();
        realm.close();
        return Observable.just(result);
    }

    public Observable<Group> updateGroup(Group group) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        GroupScheme existScheme = realm.where(GroupScheme.class).equalTo("id", group.getId()).findFirst();
        existScheme.setAvatarUrl(group.getAvatarUrl());
        existScheme.setTitle(group.getTitle());
        realm.copyToRealmOrUpdate(existScheme);
        realm.commitTransaction();
        realm.close();
        return Observable.just(group);
    }

    @NonNull private Observable<Group> getGroupFromDataBase() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<GroupScheme> query = realm.where(GroupScheme.class).equalTo("isSelected", true);
        GroupScheme first = query.findFirst();
        Group group = Group.fromScheme(first);
        realm.commitTransaction();
        realm.close();
        groupSoftReference = new SoftReference<>(group);
        return Observable.just(group);
    }

    public void changeSelectedGroup(long groupId) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.beginTransaction();
        RealmQuery<GroupScheme> query = defaultInstance.where(GroupScheme.class);
        RealmResults<GroupScheme> all = query.findAll();
        for (GroupScheme scheme : all) {
            if (scheme.getId() == groupId) {
                scheme.setSelected(true);
            } else {
                scheme.setSelected(false);
            }
        }
        defaultInstance.commitTransaction();
        defaultInstance.close();
        if (groupSoftReference != null) {
            groupSoftReference.clear();
        }

    }

    public Observable<Child> updateWithServerId(final Child child) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<ChildScheme> query =
                realm.where(ChildScheme.class).equalTo("serverId", child.getServerId());
        ChildScheme first = query.findFirst();
        if (first != null) {
            first.updateAll(child, realm, true);
            child.setId(first.getId());
            realm.commitTransaction();
            realm.close();
            return Observable.just(child);
        } else {
            int id = getFreeChildId();
            child.setId(id);
            ChildScheme childScheme = new ChildScheme(id);
            childScheme.updateAll(child, realm, true);
            realm.insertOrUpdate(childScheme);
            realm.commitTransaction();
            realm.close();
            return Observable.just(child);
        }
    }

    public Observable<List<Child>> getChildren(long groupId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<ChildScheme> all = realm.where(ChildScheme.class).findAll();
        RealmList<ChildScheme> allFromGroup = new RealmList<>();
        for (ChildScheme childScheme : all) {
            RealmList<RealmLong> groupIds = childScheme.getGroupIds();
            for (RealmLong realmLong : groupIds) {
                if (realmLong.getValue() == groupId) {
                    allFromGroup.add(childScheme);
                }
            }
        }
        List<Child> children = new ArrayList<>(all.size());
        for (ChildScheme childScheme : allFromGroup) {
            children.add(Child.fromChildScheme(childScheme));
        }
        realm.commitTransaction();
        realm.close();
        return Observable.just(children);
    }

    public Observable<List<Child>> getAllChildren() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<ChildScheme> all = realm.where(ChildScheme.class).findAll();
        List<Child> children = new ArrayList<>(all.size());
        for (ChildScheme childScheme : all) {
            children.add(Child.fromChildScheme(childScheme));
        }
        realm.commitTransaction();
        realm.close();
        return Observable.just(children);
    }


    public List<Child> deleteUnsynchronizedChildren(long groupId, List<Child> newChildren) {
        ArrayList<Long> actualIds = new ArrayList<>(newChildren.size());
        for (Child child : newChildren) {
            actualIds.add(child.getServerId());
        }
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<ChildScheme> all = realm.where(ChildScheme.class).findAll();
        RealmList<ChildScheme> allFromGroup = new RealmList<>();
        for (ChildScheme childScheme : all) {
            RealmList<RealmLong> groupIds = childScheme.getGroupIds();
            for (RealmLong childGroupId : groupIds) {
                if (childGroupId.getValue() == groupId) {
                    allFromGroup.add(childScheme);
                }
            }
        }
        RealmList<ChildScheme> childrenForDelete = new RealmList<>();
        for (ChildScheme childScheme : allFromGroup) {
            if (!actualIds.contains(childScheme.getServerId()) && childScheme.getGroupIds().size() == 1) {
                childrenForDelete.add(childScheme);
            }
        }
        RealmResults<ChildCheckScheme> exisitingChecks = realm.where(ChildCheckScheme.class).findAll();
        RealmList<ChildCheckScheme> checksForDelete = new RealmList<>();
        for (ChildCheckScheme scheme : exisitingChecks) {
            if (childrenForDelete.contains(scheme.getChild())) {
                checksForDelete.add(scheme);
            }
        }
        for (ChildCheckScheme childCheckScheme : checksForDelete) {
            childCheckScheme.deleteFromRealm();
        }
        for (ChildScheme childScheme : childrenForDelete) {
            childScheme.deleteFromRealm();
        }
        realm.commitTransaction();
        realm.close();
        return newChildren;

//
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        RealmResults<ChildCheckScheme> exisiting = realm.where(ChildCheckScheme.class).findAll();
//        for (ChildCheckScheme scheme : exisiting) {
//            ChildScheme child = scheme.getChild();
//            if (child.getServerId() == 0) {
//                child.deleteFromRealm();
//                scheme.deleteFromRealm();
//            }
//        }
//        realm.commitTransaction();
//        return Observable.just(true);
    }

    public Observable<Child> getChildById(int id) {
        Realm realm = Realm.getDefaultInstance();
        ChildScheme first = realm.where(ChildScheme.class).equalTo("id", id).findFirst();
        Child value = Child.fromChildScheme(first);
        realm.close();
        return Observable.just(value);
    }

    public Observable<List<Child>> deleteChild(long groupId, long serverId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<ChildScheme> query = realm.where(ChildScheme.class).equalTo("serverId", serverId);
        ChildScheme first = query.findFirst();
        ChildCheckScheme.deleteForChildren(realm, first);
        first.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
        return getChildren(groupId);
    }

    public Observable<Member> addInviteMember(int childId, Member member, boolean syncWithServer) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<ChildScheme> query = realm.where(ChildScheme.class).equalTo("id", childId);
        ChildScheme child = query.findFirst();
        MemberScheme memberScheme = MemberScheme.fromFamilyMember(member, syncWithServer);
        memberScheme.setSync(syncWithServer);
        memberScheme.setCached(true);
        boolean memberSchemeUpdated = false;
        RealmList<MemberScheme> inviteMembers = child.getInviteMembers();
        for (MemberScheme scheme : inviteMembers) {
            if (scheme.getEmail().equals(memberScheme.getEmail())) {
                memberSchemeUpdated = true;
                realm.insertOrUpdate(memberScheme);
            }
        }
        if (!memberSchemeUpdated) {
            child.getInviteMembers().add(memberScheme);
        }
        realm.commitTransaction();
        realm.close();
        member.setCached(true);
        return Observable.just(member);
    }

    public Observable<Member> updateInviteMember(Member member, boolean syncWithServer) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<MemberScheme> query = realm.where(MemberScheme.class)
                .equalTo("email", member.getEmail());
        MemberScheme first = query.findFirst();
        first.setSync(syncWithServer);
        first.setPosition(member.getPosition());
        first.setFirstName(member.getFirstName());
        first.setLastName(member.getLastName());
//        first.setEmail(member.getLastName());
        first.setPhone(member.getPhone());
//        String state = innerState.equals(Member.APPROVED) ? Member.APPROVED :
//                innerState.equals(Member.CASHED) ? Member.CASHED :
//                        innerState.equals(Member.EXPIRED) ? Member.EXPIRED :
//                                innerState.equals(Member.DRAFT) ? Member.DRAFT : null;

        first.setCached(member.isCached());
        first.setAdditionalPhone(member.getSecondPhone());
        realm.commitTransaction();
        realm.close();
        return Observable.just(member);
    }

    public Observable<List<Child>> getNotSyncChildren() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ChildScheme> query = realm.where(ChildScheme.class);
        RealmResults<ChildScheme> all = query.findAll();
        List<Child> children = new ArrayList<>(all.size());
        for (ChildScheme childScheme : all) {
            children.add(Child.fromChildScheme(childScheme));
        }
        List<Child> notSincChildren = new ArrayList<>();
        for (Child child : children) {
            if (child.getServerId() == 0) {
                notSincChildren.add(child);
            }
        }
        realm.close();
        return Observable.just(notSincChildren);
    }

    public Observable<List<Member>> getNotSynchronizedMembers(int childId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<ChildScheme> query = realm.where(ChildScheme.class)
                .equalTo("id", childId);
        ChildScheme first = query.findFirst();
        List<Member> notSincMembers = new ArrayList<>();
        RealmList<MemberScheme> inviteMembers = first.getInviteMembers();
        for (MemberScheme memberScheme : inviteMembers) {
            if (!memberScheme.isSync()) {
                notSincMembers.add(Member.fromScheme(memberScheme));
            }
        }
        realm.commitTransaction();
        realm.close();
        return Observable.just(notSincMembers);
    }

    public int getFreeMemberId() {
        Realm realm = Realm.getDefaultInstance();
        int id = getRandomId();
        RealmQuery<MemberScheme> query = realm.where(MemberScheme.class);
        query.equalTo("memberId", id);
        RealmResults<MemberScheme> results = query.findAll();
        if (!results.isEmpty()) {
            return getFreeMemberId();
        } else {
            realm.close();
            return id;
        }
    }

    public Observable<List<Member>> getMembersForChildrenIds(List<Long> childrenIds, boolean isCheckin) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<ChildScheme> query = realm.where(ChildScheme.class);
        query.in("id", childrenIds.toArray(new Long[childrenIds.size()]));
        List<ChildScheme> childSchemes = query.findAll();
        Map<Long, MemberScheme> parentMap = new HashMap<>();
        Map<Long, MemberScheme> resultMap = new HashMap<>();
        for (ChildScheme childScheme : childSchemes) {
            RealmList<MemberScheme> inviteMembers = childScheme.getInviteMembers();
            RealmList<MemberScheme> familyMembers = childScheme.getFamilyMembers();
            RealmList<MemberScheme> allParents = new RealmList<>();
            allParents.addAll(inviteMembers);
            allParents.addAll(familyMembers);
            for (MemberScheme parent : allParents) {
                parentMap.put(parent.getMemberId(), parent);
            }
        }
        Map<Long, MemberScheme> parentFullAccesMap = new HashMap<>();
        for (ChildScheme childScheme : childSchemes) {
            RealmList<RealmLong> fullAccessMembers = childScheme.getFullAccessMembers();
            for (RealmLong fullAccessMember : fullAccessMembers) {
                parentFullAccesMap.put(fullAccessMember.getValue(), parentMap.get(fullAccessMember.getValue()));
            }
        }

        if (isCheckin) {
            resultMap.putAll(parentMap);
        } else {
            resultMap = addMembers(childSchemes, parentFullAccesMap);
        }
        ArrayList<Member> result = new ArrayList<>(resultMap.size());
        for (Map.Entry<Long, MemberScheme> longMemberSchemeEntry : resultMap.entrySet()) {
            result.add(Member.fromScheme(longMemberSchemeEntry.getValue()));
        }
        realm.commitTransaction();
        realm.close();
        return Observable.just(result);
    }

    private Map<Long, MemberScheme> addMembers(List<ChildScheme> children,
                                               Map<Long, MemberScheme> allMembers) {
        Map<Long, MemberScheme> resultMap = new HashMap<>();
        Map<Long, MemberScheme> tempMap = new HashMap<>();
        if (children.size() == 1) {
            resultMap.putAll(allMembers);
            return resultMap;
        }
        for (int x = 0; x < children.size(); x++) {
            RealmList<RealmLong> fullAccessMembers = children.get(x).getFullAccessMembers();
            if (x == 0) {
                for (RealmLong fullAccessMember : fullAccessMembers) {
                    tempMap.put(fullAccessMember.getValue(), allMembers.get(fullAccessMember.getValue()));
                    resultMap.put(fullAccessMember.getValue(), allMembers.get(fullAccessMember.getValue()));
                }
            } else {
                resultMap.clear();
                if (fullAccessMembers.size() == 0) {
                    return resultMap;
                }
                for (RealmLong fullAccessMember : fullAccessMembers) {
                    Long key = fullAccessMember.getValue();
                    if (tempMap.containsKey(key)) {
                        resultMap.put(key, allMembers.get(key));
                    }

                }


            }
            tempMap.clear();
            tempMap.putAll(resultMap);


        }

        return resultMap;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   CHECKS  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<ChildCheck>> putChildCheck(long groupId, List<RestChildCheck> rests) {
        return Observable.from(rests)
                .flatMap(restChildCheck -> this.putChildCheck(groupId, restChildCheck))
                .toList();
    }

    public Observable<ChildCheck> putChildCheck(long groupId, RestChildCheck rest) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.beginTransaction();
        int freeChildId = getFreeChildId();
        ChildCheckScheme scheme = ChildCheckScheme.putFromRest(groupId, defaultInstance, rest, freeChildId);
        defaultInstance.commitTransaction();
        ChildCheck check = ChildCheck.fromScheme(scheme);
        defaultInstance.close();
        return Observable.just(check);
    }

    public Observable<List<ChildCheck>> putChildCheck(CheckRequest request) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.beginTransaction();
        List<ChildCheckScheme> schemes = ChildCheckScheme.putFromRequest(defaultInstance, request);
        defaultInstance.commitTransaction();
        List<ChildCheck> checks = ChildCheck.fromScheme(schemes);
        defaultInstance.close();
        return Observable.just(checks);
    }

    public Observable<List<ChildCheck>> getAllNotSynced() {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.beginTransaction();
        List<ChildCheckScheme> schemes = ChildCheckScheme.getAllNotSynced(defaultInstance);
        List<ChildCheck> checks = ChildCheck.fromScheme(schemes);
        defaultInstance.commitTransaction();
        defaultInstance.close();
        return Observable.just(checks);
    }

    public void deleteCheck(ChildCheck check) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.beginTransaction();
        ChildCheckScheme.deleteIfExist(defaultInstance, check);
        defaultInstance.commitTransaction();
        defaultInstance.close();
    }

    public void deleteState(ChildState state) {
        Realm defaultInstance = Realm.getDefaultInstance();
        defaultInstance.beginTransaction();
        ChildStateScheme.deleteIfExist(defaultInstance, state);
        defaultInstance.commitTransaction();
        defaultInstance.close();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   CHILDREN IN GROUPS  /////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<ChildInGroup>> getCachedChildrenInGroups() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<ChildInGroupScheme> all = realm.where(ChildInGroupScheme.class).findAll();
        List<ChildInGroup> result = new ArrayList<>(all.size());
        for (ChildInGroupScheme childInGroupScheme : all) {
            result.add(ChildInGroup.fromScheme(childInGroupScheme));
        }
        realm.commitTransaction();
        realm.close();
        return Observable.just(result);
    }

    public Observable<ChildInGroup> getChildInGroupByGroupId(long groupId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
//        RealmQuery<ChildInGroupScheme> first = realm.where(ChildInGroupScheme.class)
//                .equalTo("groupId", groupId).findFirst();
        RealmQuery<ChildInGroupScheme> query = realm.where(ChildInGroupScheme.class);
        ChildInGroupScheme first = query.equalTo("groupId", groupId).findFirst();
        ChildInGroup childInGroup = ChildInGroup.fromScheme(first);
        realm.commitTransaction();
        realm.close();
        return Observable.just(childInGroup);
    }

    public Observable<Boolean> updateChildrenInGroups(List<ChildInGroup> childrenInGroups) {
        long selectedChildrenId = 0;
        long selectedGroupId = 0;
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<ChildInGroupScheme> allCached = realm.where(ChildInGroupScheme.class)
                .findAll();
        for (ChildInGroupScheme childInGroupScheme : allCached) {
            if (childInGroupScheme.isSelected()) {
                selectedChildrenId = childInGroupScheme.getChildId();
                selectedGroupId = childInGroupScheme.getGroupId();
            }
        }
        allCached.deleteAllFromRealm();
        List<ChildInGroupScheme> childInGroupSchemes =
                ChildInGroupScheme.fromChildrenInGroups(childrenInGroups);
        ChildInGroupScheme wasReSelected = null;
        for (ChildInGroupScheme childInGroupScheme : childInGroupSchemes) {
            if (childInGroupScheme.getChildId() == selectedChildrenId &&
                    childInGroupScheme.getGroupId() == selectedGroupId) {
                wasReSelected = childInGroupScheme;
            }
        }
        if (wasReSelected == null) {
            if (childInGroupSchemes.size() > 0) {
                wasReSelected = childInGroupSchemes.get(0);
            }
        }
        if (wasReSelected != null) {
            wasReSelected.setSelected(true);
        }
        realm.copyToRealmOrUpdate(childInGroupSchemes);
        realm.commitTransaction();
        realm.close();
        return Observable.just(true);
    }

    public Observable<ChildInGroup> setSelectedChildInGroup(long childId, long groupId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<ChildInGroupScheme> query = realm.where(ChildInGroupScheme.class);
        query.notEqualTo("childId", childId).notEqualTo("groupId", groupId);
        RealmResults<ChildInGroupScheme> all = query.findAll();
        for (ChildInGroupScheme childInGroupScheme : all) {
            childInGroupScheme.setSelected(false);
        }
        ChildInGroupScheme first = realm.where(ChildInGroupScheme.class).findFirst();
        first.setSelected(true);
        ChildInGroup childInGroup = ChildInGroup.fromScheme(first);
        realm.commitTransaction();
        realm.close();
        return Observable.just(childInGroup);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   ALL DB  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean dropData(Boolean serverError) {
        if (serverError) return false;
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(internal -> {
                internal.deleteAll();
            });
            realm.close();
            if (groupSoftReference != null) {
                groupSoftReference.clear();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean dropData() {
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(internal -> {
                internal.deleteAll();
            });
            realm.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
