package com.ltst.core.data.realm.model;

import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.request.CheckRequest;
import com.ltst.core.data.rest.model.RestChildCheck;
import com.ltst.core.util.DateUtils;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class ChildCheckScheme extends RealmObject {

    @PrimaryKey
    private long id;
    private long serverId;
    private long groupId;
    private ChildScheme child;
    private String datetime;
    private RealmList<ChildStateScheme> childStates;
    private boolean sync;

    public ChildCheckScheme() {
    }

    public ChildCheckScheme(String datetime) {
        this.datetime = datetime;
    }

    public static ChildCheckScheme putFromRest(long groupId, Realm realm, RestChildCheck rest, int freeChildId) {
        ChildCheckScheme checkScheme = realm.where(ChildCheckScheme.class)
                .equalTo("serverId", rest.getId())
                .findFirst();
        if (checkScheme == null) {
            checkScheme = realm.createObject(ChildCheckScheme.class);
            checkScheme.id = getFreeId(realm);
            checkScheme.serverId = rest.getId();
            checkScheme.groupId = groupId;
        }
        checkScheme.child = (ChildScheme.putFromRest(realm, rest.getChild(), freeChildId));
        String date = rest.getDatetime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.RAILS_DATE_FORMAT);
        try {
            Date parsed = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        checkScheme.datetime = (rest.getDatetime());
        checkScheme.childStates = (ChildStateScheme.putFromRest(realm, rest.getChildStates()));
        checkScheme.sync = true;
        return checkScheme;
    }

    public static List<ChildCheckScheme> putFromRequest(Realm realm, CheckRequest request) {
        List<ChildCheckScheme> childCheckSchemes = new ArrayList<>(request.getChildIds().size());
        for (Long childId : request.getChildIds()) {
            ChildCheckScheme checkScheme = realm.createObject(ChildCheckScheme.class);
            checkScheme.id = getFreeId(realm);
            checkScheme.child = realm.where(ChildScheme.class)
                    .equalTo("serverId", childId)
                    .findFirst();
            checkScheme.datetime = (request.getDatetime());
            checkScheme.childStates = (ChildStateScheme.putFromRequest(realm, request));
            checkScheme.sync = false;
            childCheckSchemes.add(checkScheme);
        }
        return childCheckSchemes;
    }

    public static List<ChildCheckScheme> getAllNotSynced(Realm realm) {
        return realm.where(ChildCheckScheme.class)
                .equalTo("sync", false)
                .findAll();
    }

    public static void deleteForChildren(Realm realm, ChildScheme child) {
        RealmResults<ChildCheckScheme> checkSchemes = realm.where(ChildCheckScheme.class)
                .equalTo("child.id", child.getId())
                .findAll();
        for (int i = 0; i < checkSchemes.size(); i++) {
            List<ChildStateScheme> childStates = checkSchemes.get(i).getChildStates();
            if (childStates != null && !childStates.isEmpty()) {
                for (int x = 0; x < childStates.size(); x++) {
                    childStates.get(x).deleteFromRealm();
                }
            }
            checkSchemes.get(i).deleteFromRealm();
        }
    }

    public static void deleteIfExist(Realm realm, ChildCheck check) {
        ChildCheckScheme checkScheme = realm.where(ChildCheckScheme.class)
                .equalTo("id", check.getDbId())
                .findFirst();
        if (checkScheme != null) {
            checkScheme.deleteFromRealm();
        }
    }

    private static int getFreeId(Realm realm) {
        int id = new SecureRandom().nextInt();
        RealmQuery<ChildCheckScheme> query = realm.where(ChildCheckScheme.class);
        query.equalTo("id", id);
        RealmResults<ChildCheckScheme> results = query.findAll();
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

    public ChildScheme getChild() {
        return child;
    }

    public String getDatetime() {
        return datetime;
    }

    public RealmList<ChildStateScheme> getChildStates() {
        return childStates;
    }
}
