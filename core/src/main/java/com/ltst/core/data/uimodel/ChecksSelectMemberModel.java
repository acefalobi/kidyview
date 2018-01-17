package com.ltst.core.data.uimodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 24.09.2016.
 */

public class ChecksSelectMemberModel implements Parcelable {
    public static final int OTHER_ID = -115;

    private long memberId;
    private String iconUrl;
    private String name;
    private boolean isSelected;

    private ChecksSelectMemberModel(long memberId, String iconUrl, String name, boolean isSelected) {
        this.memberId = memberId;
        this.iconUrl = iconUrl;
        this.name = name;
        this.isSelected = isSelected;
    }

    public static ChecksSelectMemberModel getOther() {
        return new ChecksSelectMemberModel(OTHER_ID, null, null, false);
    }

    public static ChecksSelectMemberModel fromMember(Member member) {
        return new ChecksSelectMemberModel(
                member.getId(),
                member.getAvatarUrl(),
                member.getLastName() + StringUtils.SPACE + member.getFirstName(),
                false);
    }

    public static List<ChecksSelectMemberModel> fromMemberList(List<Member> members) {
        List<ChecksSelectMemberModel> models = new ArrayList<>(members.size());
        for (Member member : members) {
            models.add(fromMember(member));
        }
        return models;
    }

    public static List<Long> getIdList(List<ChecksSelectMemberModel> models) {
        List<Long> idList = new ArrayList<>(models.size());
        for (ChecksSelectMemberModel model : models) {
            idList.add(model.memberId);
        }
        return idList;
    }

    public long getMemberId() {
        return memberId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public ChecksSelectMemberModel setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.memberId);
        dest.writeString(this.iconUrl);
        dest.writeString(this.name);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected ChecksSelectMemberModel(Parcel in) {
        this.memberId = in.readLong();
        this.iconUrl = in.readString();
        this.name = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<ChecksSelectMemberModel> CREATOR = new Creator<ChecksSelectMemberModel>() {
        @Override
        public ChecksSelectMemberModel createFromParcel(Parcel source) {
            return new ChecksSelectMemberModel(source);
        }

        @Override
        public ChecksSelectMemberModel[] newArray(int size) {
            return new ChecksSelectMemberModel[size];
        }
    };
}
