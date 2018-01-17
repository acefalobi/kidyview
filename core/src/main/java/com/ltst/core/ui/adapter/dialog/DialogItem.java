package com.ltst.core.ui.adapter.dialog;


import android.os.Parcel;
import android.os.Parcelable;

import com.ltst.core.data.model.Member;
import com.ltst.core.data.response.TeacherResponse;

public class DialogItem implements Parcelable {

    private String avatarUrl;
    private String firstName;
    private String lastName;
    private boolean isChecked;
    private boolean isFakeMember;
    private String layerIdentity;

    private DialogItem() {

    }

    public String getLayerIdentity() {
        return layerIdentity;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean isFakeMember() {
        return isFakeMember;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public static DialogItem fromMember(Member member, boolean fakeItem) {
        DialogItem dialogItem = new DialogItem();
        dialogItem.avatarUrl = member.getAvatarUrl();
        dialogItem.firstName = member.getFirstName();
        dialogItem.lastName = member.getLastName();
        dialogItem.isChecked = false;
        dialogItem.isFakeMember = fakeItem;
        dialogItem.layerIdentity = member.getLayerIdentity();
        return dialogItem;
    }

    public static DialogItem fromTeacherResponse(TeacherResponse teacher, boolean fakeItem) {
        DialogItem dialogItem = new DialogItem();
        dialogItem.avatarUrl = teacher.avatarUrl;
        dialogItem.firstName = teacher.firstName;
        dialogItem.lastName = teacher.lastName;
        dialogItem.isChecked = false;
        dialogItem.isFakeMember = fakeItem;
        dialogItem.layerIdentity = teacher.layerIdentity;
        return dialogItem;
    }

    public static DialogItem fake(){
        DialogItem dialogItem = new DialogItem();
        dialogItem.isFakeMember = true;
        return dialogItem;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatarUrl);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFakeMember ? (byte) 1 : (byte) 0);
    }

    protected DialogItem(Parcel in) {
        this.avatarUrl = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.isChecked = in.readByte() != 0;
        this.isFakeMember = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DialogItem> CREATOR = new Parcelable.Creator<DialogItem>() {
        @Override public DialogItem createFromParcel(Parcel source) {
            return new DialogItem(source);
        }

        @Override public DialogItem[] newArray(int size) {
            return new DialogItem[size];
        }
    };
}

