package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.realm.model.ChildStateScheme;
import com.ltst.core.data.rest.model.RestChildState;
import com.ltst.core.data.rest.model.RestFamilyMember;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class ChildState implements Parcelable {

    private long serverId;
    private long dbId;
    private String datetime;
    private ChildStateType type;
    private String firstName;
    private String lastName;
    private long memberId;

    public ChildState(long serverId, long dbId, String datetime, ChildStateType type,
                      String firstName, String lastName, long memberId) {
        this.serverId = serverId;
        this.dbId = dbId;
        this.datetime = datetime;
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.memberId = memberId;
    }

    public static List<ChildState> fromScheme(RealmList<ChildStateScheme> schemes) {
        List<ChildState> states = new ArrayList<>(schemes.size());
        for (ChildStateScheme scheme : schemes) {
            states.add(fromScheme(scheme));
        }
        return states;
    }

    public static List<ChildState> fromResponse(List<RestChildState> restChildStates) {
        List<ChildState> childStates = new ArrayList<>(restChildStates.size());
        for (RestChildState restState : restChildStates) {
            childStates.add(fromResponse(restState));
        }
        return childStates;
    }

    public static ChildState fromResponse(RestChildState restState) {
        RestFamilyMember familyMember = restState.getFamilyMember();
        long familyMemberId = familyMember != null ? familyMember.getId() : 0;
        if (familyMember != null) {
            return new ChildState(
                    restState.getId(),
                    0,
                    restState.getDatetime(),
                    ChildStateType.fromString(restState.getKind()),
                    restState.getFamilyMember().getFirstName(),
                    restState.getFamilyMember().getLastName(),
                    familyMemberId
            );
        } else {
            return new ChildState(
                    restState.getId(),
                    0,
                    restState.getDatetime(),
                    ChildStateType.fromString(restState.getKind()),
                    restState.getFirstName(),
                    restState.getLastName(),
                    familyMemberId
            );
        }

    }

    public static ChildState fromScheme(ChildStateScheme scheme) {
        return new ChildState(scheme.getServerId(),
                scheme.getId(),
                scheme.getDatetime(),
                ChildStateType.fromString(scheme.getType()),
                scheme.getFirstName(),
                scheme.getLastName(),
                scheme.getMemberId());
    }

    public static List<ChildState> allFromChecks(List<ChildCheck> checks) {
        List<ChildState> result = new ArrayList<>();
        for (ChildCheck check : checks) {
            List<ChildState> itemStates = check.getChildStates();
            if (itemStates != null) {
                for (ChildState itemState : itemStates) {
                    result.add(itemState);
                }
            }
        }
        return result;
    }

    public long getServerId() {
        return serverId;
    }

    public long getDbId() {
        return dbId;
    }

    public String getDatetime() {
        return datetime;
    }

    public ChildStateType getType() {
        return type;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getMemberId() {
        return memberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.serverId);
        dest.writeLong(this.dbId);
        dest.writeString(this.datetime);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeLong(this.memberId);
    }

    protected ChildState(Parcel in) {
        this.serverId = in.readLong();
        this.dbId = in.readLong();
        this.datetime = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : ChildStateType.values()[tmpType];
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.memberId = in.readLong();
    }

    public static final Creator<ChildState> CREATOR = new Creator<ChildState>() {
        @Override
        public ChildState createFromParcel(Parcel source) {
            return new ChildState(source);
        }

        @Override
        public ChildState[] newArray(int size) {
            return new ChildState[size];
        }
    };
}
