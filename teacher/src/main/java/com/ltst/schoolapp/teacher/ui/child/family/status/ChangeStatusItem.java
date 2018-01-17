package com.ltst.schoolapp.teacher.ui.child.family.status;


public class ChangeStatusItem {
    private Status status;
    private boolean isChecked;

    public ChangeStatusItem(Status status, boolean isChecked) {
        this.status = status;
        this.isChecked = isChecked;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
