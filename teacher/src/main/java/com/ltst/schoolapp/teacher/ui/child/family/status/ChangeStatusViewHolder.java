package com.ltst.schoolapp.teacher.ui.child.family.status;


import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.schoolapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeStatusViewHolder
        extends BindableViewHolder<ChangeStatusItem, BindableViewHolder.ActionListener<ChangeStatusItem>> {

    @BindView(R.id.change_status_item_root) ViewGroup rootView;
    @BindView(R.id.change_status_item_text) TextView statusField;
    @BindView(R.id.change_status_item_checkbox) AppCompatRadioButton checkBox;

    public ChangeStatusViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void bindView(int position, ChangeStatusItem item, ActionListener<ChangeStatusItem> actionListener) {
        super.bindView(position, item, actionListener);
        statusField.setText(item.getStatus().getDefault());
        checkBox.setChecked(item.isChecked());
        rootView.setOnClickListener(v -> actionListener.OnItemClickListener(position, item));
    }
}
