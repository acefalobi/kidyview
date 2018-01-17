package com.ltst.schoolapp.teacher.ui.child.family.status;


import com.ltst.core.data.model.Member;

public class ChangeStatusMemberWrapper {
    private Member member;
    private boolean wasChanged;

    public ChangeStatusMemberWrapper(Member member) {
        this.member = member;
    }

    public void setWasChanged(boolean wasChanged) {
        this.wasChanged = wasChanged;
    }

    public boolean wasChanged() {
        return wasChanged;
    }

    public Member getMember() {
        return member;
    }
}
