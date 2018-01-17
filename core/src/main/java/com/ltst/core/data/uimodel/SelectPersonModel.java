package com.ltst.core.data.uimodel;

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
public class SelectPersonModel implements Parcelable {

    public static final int GROUP_POSITION = 0;
    public static final int GROUP_ID = -115;
    public static final String GROUP_HOLDER = "SelectPersonModel.GroupAvatar";
    private long dbId;
    private long serverId;
    private String iconUrl;
    private String name;
    private boolean isSelected;

    public SelectPersonModel(long dbId, long serverId, String iconUrl, String name, boolean isSelected) {
        this.dbId = dbId;
        this.serverId = serverId;
        this.iconUrl = iconUrl;
        this.name = name;
        this.isSelected = isSelected;
    }

    public static SelectPersonModel getEmptyGroup() {
        return new SelectPersonModel(GROUP_ID, GROUP_ID, null, null, false);
    }

    public static SelectPersonModel fromChild(Child child) {
        return new SelectPersonModel(
                child.getId(),
                child.getServerId(),
                child.getAvatarUrl(),
                child.getLastName() + StringUtils.SPACE + child.getFirstName(),
                false);
    }

    public static List<SelectPersonModel> fromChildList(List<Child> children) {
        List<SelectPersonModel> models = new ArrayList<>(children.size());
        for (Child child : children) {
            models.add(fromChild(child));
        }
        return models;
    }

    public static List<Long> getServerIdList(List<SelectPersonModel> models) {
        List<Long> idList = new ArrayList<>(models.size());
        for (SelectPersonModel model : models) {
            idList.add(model.serverId);
        }
        return idList;
    }

    public static List<Long> getDBIdList(List<SelectPersonModel> models) {
        List<Long> idList = new ArrayList<>(models.size());
        for (SelectPersonModel model : models) {
            idList.add(model.dbId);
        }
        return idList;
    }

    public long getServerId() {
        return serverId;
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

    public SelectPersonModel setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }

    public static SelectPersonModel fromGroup(Group group) {
        String url = group.getAvatarUrl();
        if (url == null) {
            url = GROUP_HOLDER;
        }
        return new SelectPersonModel(GROUP_ID, GROUP_ID, url, group.getTitle(), false);
    }

    public static class DiffCallback extends DiffUtil.Callback {

        private final List<SelectPersonModel> oldList;
        private final List<SelectPersonModel> newList;

        public DiffCallback(List<SelectPersonModel> oldList, List<SelectPersonModel> newList) {
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
            return oldList.get(oldItemPosition).getServerId() == newList.get(newItemPosition).getServerId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final SelectPersonModel oldItem = oldList.get(oldItemPosition);
            final SelectPersonModel newItem = newList.get(newItemPosition);
            boolean theSame = oldItem.getServerId() == newItem.getServerId();
            if (theSame) {
                newItem.setSelected(oldItem.isSelected());
            }
            return theSame;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectPersonModel model = (SelectPersonModel) o;

        return serverId == model.serverId;

    }

    @Override
    public int hashCode() {
        return (int) serverId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.dbId);
        dest.writeLong(this.serverId);
        dest.writeString(this.iconUrl);
        dest.writeString(this.name);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected SelectPersonModel(Parcel in) {
        this.dbId = in.readLong();
        this.serverId = in.readLong();
        this.iconUrl = in.readString();
        this.name = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<SelectPersonModel> CREATOR = new Creator<SelectPersonModel>() {
        @Override
        public SelectPersonModel createFromParcel(Parcel source) {
            return new SelectPersonModel(source);
        }

        @Override
        public SelectPersonModel[] newArray(int size) {
            return new SelectPersonModel[size];
        }
    };
}
