package com.ltst.core.ui.adapter.dialog;


import android.view.View;

import com.danil.recyclerbindableadapter.library.FilterBindableAdapter;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.ui.holder.DialogMemberViewHolder;

public class DialogMemberAdapter extends FilterBindableAdapter<DialogItem, DialogMemberViewHolder> {

    private final BindableViewHolder.ActionListener<DialogItem> actionListener;
    private final boolean showCheckBox;

    public DialogMemberAdapter(BindableViewHolder.ActionListener<DialogItem> actionListener, boolean showCheckBox) {
        this.actionListener = actionListener;
        this.showCheckBox = showCheckBox;
    }

    @Override protected String itemToString(DialogItem item) {
        return item.getFirstName() + StringUtils.SPACE + item.getLastName();
    }

    @Override protected void onBindItemViewHolder(DialogMemberViewHolder viewHolder, int position, int type) {
        viewHolder.bindView(position, getItem(position), actionListener, showCheckBox);
    }

    @Override protected DialogMemberViewHolder viewHolder(View view, int type) {
        return new DialogMemberViewHolder(view);
    }

    @Override protected int layoutId(int type) {
        return R.layout.viewholder_select_dialog_member;
    }


}
