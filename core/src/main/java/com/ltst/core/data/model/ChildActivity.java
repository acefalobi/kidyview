package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.rest.model.RestChildActivity;


/**
 * Created by Danil on 26.09.2016.
 */

public class ChildActivity implements Parcelable {
    private int id;
    private String title;
    private String iconUrl;

    public ChildActivity(int id, String title, String iconUrl) {
        this.id = id;
        this.title = title;
        this.iconUrl = iconUrl;
    }

    public static ChildActivity fromRest(RestChildActivity rest) {
        return new ChildActivity(rest.getId(), rest.getTitle(), rest.getIconUrl());
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getIconUrl() {
        return iconUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.iconUrl);
    }

    protected ChildActivity(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.iconUrl = in.readString();
    }

    public static final Parcelable.Creator<ChildActivity> CREATOR = new Parcelable.Creator<ChildActivity>() {
        @Override
        public ChildActivity createFromParcel(Parcel source) {
            return new ChildActivity(source);
        }

        @Override
        public ChildActivity[] newArray(int size) {
            return new ChildActivity[size];
        }
    };
}
