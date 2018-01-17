package com.ltst.schoolapp.teacher.ui.main.children;

import android.support.v7.util.DiffUtil;

import com.ltst.core.data.model.Child;
import com.ltst.core.ui.adapter.ChildrenAdapter;

import java.util.List;

public class ChildrenDiffCallback extends DiffUtil.Callback {

    private final List<ChildrenAdapter.ChildWrapper> oldList;
    private final List<ChildrenAdapter.ChildWrapper> newList;

    public ChildrenDiffCallback(List<ChildrenAdapter.ChildWrapper> oldList,
                                List<ChildrenAdapter.ChildWrapper> newList) {
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
        Child oldChild = oldList.get(oldItemPosition).getChild();
        Child newChild = newList.get(newItemPosition).getChild();
        return oldChild.getId() == newChild.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Child oldItem = oldList.get(oldItemPosition).getChild();
        final Child newItem = newList.get(newItemPosition).getChild();
        return oldItem.equals(newItem);

//        final String oldAvatar = oldItem.getAvatarUrl();
//        final String newAvatar = newItem.getAvatarUrl();
//        if (oldAvatar != null && newAvatar != null) {
//            return oldItem.getFirstName().equals(newItem.getFirstName()) &&
//                    oldItem.getLastName().equals(newItem.getLastName()) &&
//                    oldAvatar.equals(newAvatar);
//        } else {
//            return oldItem.getFirstName().equals(newItem.getFirstName()) &&
//                    oldItem.getLastName().equals(newItem.getLastName());
//        }
//
    }
}
