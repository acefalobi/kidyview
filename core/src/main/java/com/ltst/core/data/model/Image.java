package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.rest.model.RestImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 22.09.2016.
 */

public class Image implements Parcelable {
    private String resourceToken;
    private String url;

    public Image(String resourceToken, String url) {
        this.resourceToken = resourceToken;
        this.url = url;
    }

    public static List<Image> fromRest(List<RestImage> restImages) {
        List<Image> images = new ArrayList<>(restImages.size());
        for (RestImage restImage : restImages) {
            images.add(fromRest(restImage));
        }
        return images;
    }

    public static Image fromRest(RestImage restImage) {
        return new Image(restImage.getResourceToken(), restImage.getUrl());
    }

    public String getResourceToken() {
        return resourceToken;
    }

    public String getUrl() {
        return url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.resourceToken);
        dest.writeString(this.url);
    }

    protected Image(Parcel in) {
        this.resourceToken = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
