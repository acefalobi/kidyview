package com.ltst.core.data.realm.model;


import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.request.CheckRequest;
import com.ltst.core.data.rest.model.RestChildState;
import com.ltst.core.data.uimodel.ChecksSelectMemberModel;

import java.security.SecureRandom;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ChildStateScheme extends RealmObject {

    @PrimaryKey
    private long id;
    private long serverId;
    private String datetime;
    private String type;
    private long memberId;
    private String firstName;
    private String lastName;

    public static RealmList<ChildStateScheme> putFromRequest(Realm realm, CheckRequest request) {
        RealmList<ChildStateScheme> childStateSchemes = new RealmList<>();
        ChildStateScheme stateScheme = realm.createObject(ChildStateScheme.class);
        stateScheme.id = getFreeId(realm);
        stateScheme.datetime = request.getDatetime();
        stateScheme.type = request.isCheckIn() ? "checkin" : "checkout";
        if (request.getResponsible().equals(CheckRequest.REGISTERED)) {
            MemberScheme memberScheme = realm.where(MemberScheme.class)
                    .equalTo("memberId", request.getFamilyMemberId())
                    .findFirst();
            stateScheme.memberId = memberScheme.getMemberId();
            stateScheme.firstName = memberScheme.getFirstName();
            stateScheme.lastName = memberScheme.getLastName();
        } else {
            stateScheme.memberId = ChecksSelectMemberModel.OTHER_ID;
            stateScheme.firstName = request.getFirstName();
            stateScheme.lastName = request.getLastName();
        }
        childStateSchemes.add(stateScheme);
        return childStateSchemes;
    }

    public static RealmList<ChildStateScheme> putFromRest(Realm realm, List<RestChildState> rests) {
        RealmList<ChildStateScheme> childStateSchemes = new RealmList<>();
        for (RestChildState rest : rests) {
            childStateSchemes.add(putFromRest(realm, rest));
        }
        return childStateSchemes;
    }

    public static ChildStateScheme putFromRest(Realm realm, RestChildState rest) {
        ChildStateScheme stateScheme = realm.where(ChildStateScheme.class)
                .equalTo("serverId", rest.getId())
                .findFirst();
        if (stateScheme == null) {
            stateScheme = realm.createObject(ChildStateScheme.class);
            stateScheme.id = getFreeId(realm);
            stateScheme.serverId = rest.getId();
        }
        stateScheme.datetime = rest.getDatetime();
        stateScheme.type = rest.getKind();
        stateScheme.firstName = rest.getFamilyMember() == null
                ? rest.getFirstName()
                : rest.getFamilyMember().getFirstName();
        stateScheme.lastName = rest.getFamilyMember() == null
                ? rest.getLastName()
                : rest.getFamilyMember().getLastName();
        return stateScheme;
    }

    public static void deleteIfExist(Realm realm, ChildState state) {
        ChildStateScheme stateScheme = realm.where(ChildStateScheme.class)
                .equalTo("id", state.getDbId())
                .findFirst();
        if (stateScheme != null) {
            stateScheme.deleteFromRealm();
        }
    }

    private static int getFreeId(Realm realm) {
        int id = new SecureRandom().nextInt();
        RealmQuery<ChildStateScheme> query = realm.where(ChildStateScheme.class);
        query.equalTo("id", id);
        RealmResults<ChildStateScheme> results = query.findAll();
        if (!results.isEmpty()) {
            return getFreeId(realm);
        } else {
            return id;
        }
    }

    public long getId() {
        return id;
    }

    public long getServerId() {
        return serverId;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getType() {
        return type;
    }

    public long getMemberId() {
        return memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
