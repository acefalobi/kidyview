package com.ltst.schoolapp.teacher.ui.addchild.fragment;


import com.ltst.core.data.model.Group;

import java.util.ArrayList;
import java.util.List;

public class SelectableGroup {
    private Group group;
    private boolean isSelected;

    public SelectableGroup(Group group, boolean isSelected) {
        this.group = group;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    static List<Group> getGroups(List<SelectableGroup> selectableGroups) {
        List<Group> result = new ArrayList<>();
        for (SelectableGroup selectableGroup : selectableGroups) {
            result.add(selectableGroup.getGroup());
        }
        return result;
    }

    static List<Group> getSelectedGroups(List<SelectableGroup> selectableGroups) {
        List<Group> result = new ArrayList<>();
        for (SelectableGroup selectableGroup : selectableGroups) {
            if (selectableGroup.isSelected()) {
                result.add(selectableGroup.getGroup());
            }
        }
        return result;
    }

    public static List<SelectableGroup> fromGroups(long selectedGroupId, List<Group> groups) {
        List<SelectableGroup> result = new ArrayList<>(groups.size());
        for (Group group : groups) {
            result.add(new SelectableGroup(group, group.getId() == selectedGroupId));
        }
        return result;
    }

    public static List<SelectableGroup> fromGroups(List<Group> groups) {
        List<SelectableGroup> result = new ArrayList<>(groups.size());
        for (Group group : groups) {
            result.add(new SelectableGroup(group, group.isSelected()));
        }
        return result;
    }
}
