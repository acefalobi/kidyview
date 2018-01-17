package com.ltst.core.data.realm.model;


import com.ltst.core.data.model.ChildInGroup;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ChildInGroupScheme extends RealmObject {

    private long childId;

    private String firtName;

    private String lastName;

    private long groupId;

    private String groupTitle;

    private boolean isSelected;

    private String groupAvatarUrl;

    private String childAvatarUrl;

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public void setFirtName(String firtName) {
        this.firtName = firtName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Long getChildId() {
        return childId;
    }

    public String getFirtName() {
        return firtName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getGroupAvatarUrl() {
        return groupAvatarUrl;
    }

    public String getChildAvatarUrl() {
        return childAvatarUrl;
    }

    public void setGroupAvatarUrl(String groupAvatarUrl) {
        this.groupAvatarUrl = groupAvatarUrl;
    }

    public void setChildAvatarUrl(String childAvatarUrl) {
        this.childAvatarUrl = childAvatarUrl;
    }

    public static List<ChildInGroupScheme> fromChildrenInGroups(List<ChildInGroup> childInGroupList) {
        RealmList<ChildInGroupScheme> result = new RealmList<>();
        for (ChildInGroup childInGroup : childInGroupList) {
            ChildInGroupScheme scheme = new ChildInGroupScheme();
            scheme.setChildId(childInGroup.getChildId());
            scheme.setFirtName(childInGroup.getFirtName());
            scheme.setLastName(childInGroup.getLastName());
            scheme.setGroupId(childInGroup.getGroupId());
            scheme.setGroupTitle(childInGroup.getGroupTitle());
            scheme.setSelected(false);
            scheme.setGroupAvatarUrl(childInGroup.getGroupAvatarUrl());
            scheme.setChildAvatarUrl(childInGroup.getChildAvatarUrl());
            result.add(scheme);
        }
        return result;
    }
}
