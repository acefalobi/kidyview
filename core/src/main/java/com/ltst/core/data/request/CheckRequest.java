package com.ltst.core.data.request;

import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.model.ChildStateType;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.uimodel.ChecksSelectMemberModel;
import com.ltst.core.util.DateUtils;
import com.squareup.moshi.Json;

import java.util.Arrays;
import java.util.List;

public class CheckRequest {
    public static final String REGISTERED = "registered";
    public static final String UNREGISTER = "unregistered";

    @Json(name = "child_ids")
    private List<Long> childIds;
    private String datetime;
    private String responsible;
    @Json(name = "family_member_id")
    private Long familyMemberId;
    @Json(name = "first_name")
    private String firstName;
    @Json(name = "last_name")
    private String lastName;
    private String code;
    private boolean isCheckIn;

    public static CheckRequest checkIn(List<Long> childIds, Member member) {
        CheckRequest checkRequest = new CheckRequest();
        checkRequest.childIds = childIds;
        checkRequest.datetime = DateUtils.getTime();
        if (member.getId() == ChecksSelectMemberModel.OTHER_ID) {
            checkRequest.responsible = UNREGISTER;
        } else {
            checkRequest.responsible = REGISTERED;
        }
        checkRequest.firstName = member.getFirstName();
        checkRequest.lastName = member.getLastName();
        checkRequest.familyMemberId = member.getId();
        checkRequest.isCheckIn = true;
        return checkRequest;
    }

    public static CheckRequest checkOut(List<Long> childIds, Member member, String code) {
        CheckRequest checkRequest = new CheckRequest();
        checkRequest.childIds = childIds;
        checkRequest.datetime = DateUtils.getTime();
        if (member.getId() == ChecksSelectMemberModel.OTHER_ID) {
            checkRequest.responsible = UNREGISTER;
            checkRequest.firstName = member.getFirstName();
            checkRequest.lastName = member.getLastName();
            checkRequest.code = code;
        } else {
            checkRequest.responsible = REGISTERED;
            checkRequest.familyMemberId = member.getId();
        }
        checkRequest.isCheckIn = false;
        return checkRequest;
    }


    public static CheckRequest fromCheck(ChildCheck childCheck, ChildState state) {
        List<Long> childIds = Arrays.asList(childCheck.getChild().getServerId());
        Member member = Member.fromChildState(state);
        CheckRequest request;
        if (state.getType().equals(ChildStateType.CHECKIN)) {
            request = CheckRequest.checkIn(childIds, member);
        } else {
            request = CheckRequest.checkOut(childIds, member, null);
        }
        return request;
    }

    public List<Long> getChildIds() {
        return childIds;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getResponsible() {
        return responsible;
    }

    public Long getFamilyMemberId() {
        return familyMemberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCode() {
        return code;
    }

    public boolean isCheckIn() {
        return isCheckIn;
    }
}
