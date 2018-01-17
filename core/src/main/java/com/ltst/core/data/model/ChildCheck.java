package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.realm.model.ChildCheckScheme;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.rest.model.RestChildCheck;
import com.ltst.core.data.rest.model.RestChildState;

import java.util.ArrayList;
import java.util.List;

public class ChildCheck implements Parcelable {

    private long dbId;
    private long serverId;
    private Child child;
    private String datetime;
    private List<ChildState> childStates;

    public ChildCheck(long dbId, long serverId, Child child, String datetime, List<ChildState>
            childStates) {
        this.dbId = dbId;
        this.serverId = serverId;
        this.child = child;
        this.datetime = datetime;
        this.childStates = childStates;
    }

    public static List<ChildCheck> fromScheme(List<ChildCheckScheme> schemes) {
        List<ChildCheck> checks = new ArrayList<>(schemes.size());
        for (ChildCheckScheme scheme : schemes) {
            checks.add(fromScheme(scheme));
        }
        return checks;
    }

    public static ChildCheck fromScheme(ChildCheckScheme scheme) {
        return new ChildCheck(
                scheme.getId(),
                scheme.getServerId(),
                Child.fromChildScheme(scheme.getChild()),
                scheme.getDatetime(),
                ChildState.fromScheme(scheme.getChildStates()));
    }

    public static ChildCheck fromResponse(RestChildCheck childCheckResponse) {
        ChildResponse childResponse = childCheckResponse.getChild();
        Child child = Child.fromResponse(childResponse);
        List<RestChildState> restStates = childCheckResponse.getChildStates();
        List<ChildState> childStates = ChildState.fromResponse(restStates);
        return new ChildCheck(
                0,
                childCheckResponse.getId(),
                child, childCheckResponse.getDatetime(), childStates);
    }

    public long getDbId() {
        return dbId;
    }

    public Long getServerId() {
        return serverId;
    }

    public Child getChild() {
        return child;
    }

    public String getDatetime() {
        return datetime;
    }

    public List<ChildState> getChildStates() {
        return childStates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.serverId);
        dest.writeParcelable(this.child, flags);
        dest.writeString(this.datetime);
        dest.writeTypedList(this.childStates);
    }

    protected ChildCheck(Parcel in) {
        this.serverId = (Long) in.readValue(Long.class.getClassLoader());
        this.child = in.readParcelable(Child.class.getClassLoader());
        this.datetime = in.readString();
        this.childStates = in.createTypedArrayList(ChildState.CREATOR);
    }

    public static final Creator<ChildCheck> CREATOR = new Creator<ChildCheck>() {
        @Override
        public ChildCheck createFromParcel(Parcel source) {
            return new ChildCheck(source);
        }

        @Override
        public ChildCheck[] newArray(int size) {
            return new ChildCheck[size];
        }
    };
}
