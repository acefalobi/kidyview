package com.ltst.core.data.realm.model;

import com.ltst.core.data.model.Group;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GroupScheme extends RealmObject {

    @PrimaryKey
    private long id;
    private String title;
    private String avatarUrl;
    private boolean isSelected;

    public GroupScheme() {
    }

    public GroupScheme(long id, String title, String avatarUrl) {
        this.id = id;
        this.title = title;
        this.avatarUrl = avatarUrl;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public static List<GroupScheme> fromGroups(List<Group> groups) {
        List<GroupScheme> schemes = new ArrayList<>(groups.size());
        for (Group group : groups) {
            schemes.add(new GroupScheme(group.getId(), group.getTitle(), group.getAvatarUrl()));
        }
        return schemes;
    }
}
