package com.ltst.schoolapp.teacher.ui.child.family.status;


import com.ltst.core.data.model.Member;

public enum Status {

    LIMITED(Member.LIMITED_ACCESS),
    FULL(Member.FULL_ACCESS);

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getDefault() {
        return status;
    }
}
