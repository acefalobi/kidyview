package com.ltst.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.util.DiffUtil;

import com.ltst.core.data.realm.model.MemberScheme;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.rest.model.RestFamilyMember;
import com.ltst.core.data.uimodel.ChecksSelectMemberModel;

import java.util.ArrayList;
import java.util.List;

public class Member implements Parcelable {

    private static final String STATUS_LIMITED = "limited";
    private static final String STATUS_FULL_ACCESS = "full access";

    public static final long FAKE_MEMBER_ID = -220;

    private long familyId;
    private long id;
    private String position;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String secondPhone;
    private String avatarUrl;
    private String accessLevel;
    private boolean cached;
    private String layerIdentity;

    public Member(long id) {
        this.id = id;
    }

    // Empty constructor for Realm needed, don`t use it!
    public Member() {
    }

    public static Member createOther(String firstName, String lastName) {
        Member member = new Member();
        member.id = ChecksSelectMemberModel.OTHER_ID;
        member.firstName = firstName;
        member.lastName = lastName;
        return member;
    }

    public static Member createForId(long id) {
        Member member = new Member();
        member.id = id;
        return member;
    }

    public static Member fromRestMember (RestFamilyMember restMemeber){
        Member member = new Member(restMemeber.getId());
        member.firstName = restMemeber.getFirstName();
        member.lastName = restMemeber.getLastName();
        member.email = restMemeber.getEmail();
        member.phone = restMemeber.getPhone();
        member.secondPhone = restMemeber.getAdditionalPhone();
        member.avatarUrl = restMemeber.getAvatarUrl();
        return member;
    }

    public static Member fromChildState(ChildState state) {
        Member member = new Member();
        member.id = state.getMemberId();
        member.firstName = state.getFirstName();
        member.lastName = state.getLastName();
        return member;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(long familyId) {
        this.familyId = familyId;
    }

    public String getLayerIdentity() {
        return layerIdentity;
    }

    public static List<Member> fromPosition(List<ChildResponse.Position> positions) {
        List<Member> members = new ArrayList<>(positions.size());
        for (ChildResponse.Position position : positions) {
            members.add(fromPosition(position));
        }
        return members;
    }

    public void setLayerIdentity(String layerIdentity) {
        this.layerIdentity = layerIdentity;
    }

    public static Member fromPosition(ChildResponse.Position position) {
        ChildResponse.FamilyMemberResponse familyMemberResponse = position.getFamilyMemberResponse();
        Member member = new Member(familyMemberResponse.getMemberId());
        member.position = position.getPosition();
        member.firstName = familyMemberResponse.getFirstName();
        member.lastName = familyMemberResponse.getLastName();
        member.layerIdentity = familyMemberResponse.getLayerIdentity();
        member.email = familyMemberResponse.getEmail();
        member.phone = familyMemberResponse.getPhone();
        member.avatarUrl = familyMemberResponse.getAvatarUrl();
        member.accessLevel = position.getAccessLevel();
        member.familyId = position.getFamilyId();

        return member;
    }

    public static final String LIMITED_ACCESS = "limited";
    public static final String FULL_ACCESS = "full access";

//    @Retention(RetentionPolicy.SOURCE)
//    @StringDef({LIMITED_ACCESS, FULL_ACCESS})
//    public @interface ACCESS_LEVEL {
//
//    }

    public static String determinateAccessLevelDef(String string) {
        if (string.equals(LIMITED_ACCESS)) {
            return LIMITED_ACCESS;
        } else if (string.equals(FULL_ACCESS)) {
            return FULL_ACCESS;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (id != member.id) return false;
        if (position != null ? !position.equals(member.position) : member.position != null)
            return false;
        if (firstName != null ? !firstName.equals(member.firstName) : member.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(member.lastName) : member.lastName != null)
            return false;
        if (email != null ? !email.equals(member.email) : member.email != null) return false;
        if (phone != null ? !phone.equals(member.phone) : member.phone != null) return false;
        if (secondPhone != null ? !secondPhone.equals(member.secondPhone) : member.secondPhone != null)
            return false;
        return avatarUrl != null ? !avatarUrl.equals(member.avatarUrl) : member.avatarUrl != null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (secondPhone != null ? secondPhone.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (accessLevel != null ? accessLevel.hashCode() : 0);
        result = 31 * result + (accessLevel != null ? accessLevel.hashCode() : 0);
        return result;
    }

    public Member copy() {
        Member member = new Member(this.id);
        member.setPosition(this.position);
        member.setFirstName(this.firstName);
        member.setLastName(this.lastName);
        member.setEmail(this.email);
        member.setPhone(this.phone);
        member.setSecondPhone(this.secondPhone);
        member.setAvatarUrl(this.avatarUrl);
        member.setAccessLevel(this.accessLevel);
        member.setFamilyId(this.familyId);
        return member;
    }

    public static Member fromScheme(MemberScheme memberScheme) {
        Member member = new Member(memberScheme.getMemberId());
        member.position = memberScheme.getPosition();
        member.firstName = memberScheme.getFirstName();
        member.lastName = memberScheme.getLastName();
        member.email = memberScheme.getEmail();
        member.phone = memberScheme.getPhone();
        member.secondPhone = memberScheme.getAdditionalPhone();
        member.avatarUrl = memberScheme.getAvatarUrl();
        member.accessLevel = memberScheme.getAccessLevel();
        member.layerIdentity = memberScheme.getLayerIdentity();
        member.setFamilyId(memberScheme.getFamilyId());
        return member;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.position);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.secondPhone);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.accessLevel);
        dest.writeByte(this.cached ? (byte) 1 : (byte) 0);
    }

    protected Member(Parcel in) {
        this.id = in.readLong();
        this.position = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.secondPhone = in.readString();
        this.avatarUrl = in.readString();
        this.accessLevel = in.readString();
        this.cached = in.readByte() != 0;
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override public Member createFromParcel(Parcel source) {
            return new Member(source);
        }

        @Override public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public static class DiffCallback extends DiffUtil.Callback {

        private List<Member> oldMembers;
        private List<Member> newMembers;

        public DiffCallback(List<Member> oldMembers, List<Member> newMembers) {
            this.oldMembers = oldMembers;
            this.newMembers = newMembers;
        }

        @Override public int getOldListSize() {
            return oldMembers.size();
        }

        @Override public int getNewListSize() {
            return newMembers.size();
        }

        @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldMembers.get(oldItemPosition).id == newMembers.get(newItemPosition).id;
        }

        @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldMembers.get(oldItemPosition).equals(newMembers.get(newItemPosition));
        }
    }
}
