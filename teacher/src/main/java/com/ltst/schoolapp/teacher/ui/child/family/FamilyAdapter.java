package com.ltst.schoolapp.teacher.ui.child.family;

import android.view.View;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.data.model.Member;
import com.ltst.core.ui.holder.FamilyViewHolder;
import com.ltst.schoolapp.R;

public class FamilyAdapter extends RecyclerBindableAdapter<Member,FamilyViewHolder>{

    private final boolean canChangeStatus;
    private final FamilyViewHolder.MemberClickListener actionListener;

    public FamilyAdapter(boolean canChangeStatus, FamilyViewHolder.MemberClickListener actionListener) {
        this.canChangeStatus = canChangeStatus;
        this.actionListener = actionListener;
    }

    @Override protected void onBindItemViewHolder(FamilyViewHolder viewHolder, int position, int type) {
            viewHolder.bindViewWithAccess(position, getItem(position),actionListener, canChangeStatus);
    }



    @Override protected FamilyViewHolder viewHolder(View view, int type) {
        return new FamilyViewHolder(view);
    }

    @Override protected int layoutId(int type) {
        return R.layout.viewholder_member;
    }

}
