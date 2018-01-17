package com.ltst.schoolapp.teacher.ui.checks.single.check.fragment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.util.DiffUtil;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 24.09.2016.
 */

public class SingleCheckModel implements Parcelable {
    public static final int GROUP_POSITION = 0;
    public static final int GROUP_ID = -115;

    private long childId;
    private String iconUrl;
    private String name;
    private boolean isSelected;

    private SingleCheckModel(long childId, String iconUrl, String name, boolean isSelected) {
        this.childId = childId;
        this.iconUrl = iconUrl;
        this.name = name;
        this.isSelected = isSelected;
    }

    public static SingleCheckModel getEmptyGroup() {
        return new SingleCheckModel(GROUP_ID, null, null, false);
    }

    public static SingleCheckModel fromChild(Child child) {
        return new SingleCheckModel(
                child.getServerId(),
                child.getAvatarUrl(),
                child.getLastName() + StringUtils.SPACE + child.getFirstName(),
                false);
    }

    public static List<SingleCheckModel> fromChildList(List<Child> children) {
        List<SingleCheckModel> models = new ArrayList<>(children.size());
        for (Child child : children) {
            models.add(fromChild(child));
        }
        return models;
    }

    public static List<Long> getIdList(List<SingleCheckModel> models) {
        List<Long> idList = new ArrayList<>(models.size());
        for (SingleCheckModel model : models) {
            idList.add(model.childId);
        }
        return idList;
    }

    public long getChildId() {
        return childId;
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

    public SingleCheckModel setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }

    public static SingleCheckModel fromGroup(Group group) {
        return new SingleCheckModel(GROUP_ID, group.getAvatarUrl(), group.getTitle(), false);
    }

    public static class DiffCallback extends DiffUtil.Callback {

        private final List<SingleCheckModel> oldList;
        private final List<SingleCheckModel> newList;

        public DiffCallback(List<SingleCheckModel> oldList, List<SingleCheckModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getChildId() == newList.get(newItemPosition).getChildId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final SingleCheckModel oldItem = oldList.get(oldItemPosition);
            final SingleCheckModel newItem = newList.get(newItemPosition);

            return oldItem.getName().equals(newItem.getName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SingleCheckModel model = (SingleCheckModel) o;

        return childId == model.childId;

    }

    @Override
    public int hashCode() {
        return (int) childId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.childId);
        dest.writeString(this.iconUrl);
        dest.writeString(this.name);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected SingleCheckModel(Parcel in) {
        this.childId = in.readLong();
        this.iconUrl = in.readString();
        this.name = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<SingleCheckModel> CREATOR = new Creator<SingleCheckModel>() {
        @Override
        public SingleCheckModel createFromParcel(Parcel source) {
            return new SingleCheckModel(source);
        }

        @Override
        public SingleCheckModel[] newArray(int size) {
            return new SingleCheckModel[size];
        }
    };
}
