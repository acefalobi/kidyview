package com.ltst.schoolapp.teacher.ui.checks.select.child.fragment;


import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.core.data.model.Group;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.addchild.fragment.SelectableGroup;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseGroupHolder extends BindableViewHolder<SelectableGroup, BindableViewHolder.ActionListener<SelectableGroup>> {

    @BindView(R.id.select_group_item_radio_button) AppCompatRadioButton radioButton;
    @BindView(R.id.select_group_item_image) ImageView imageView;
    @BindView(R.id.select_group_item_text) TextView title;
    @BindView(R.id.select_group_item_root) ViewGroup rootView;

    public ChooseGroupHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @BindDimen(R.dimen.add_child_group_item_avatar_size) int avatarSize;

    @Override public void bindView(int position, SelectableGroup item, ActionListener<SelectableGroup> actionListener) {
        super.bindView(position, item, actionListener);
        radioButton.setChecked(item.isSelected());
        radioButton.setClickable(false);
        rootView.setOnClickListener(v -> {
            actionListener.OnItemClickListener(position, item);
        });
        Group group = item.getGroup();
        title.setText(group.getTitle());
        Glide.with(imageView.getContext())
                .load(group.getAvatarUrl())
                .asBitmap()
                .override(avatarSize, avatarSize)
                .centerCrop()
                .error(R.drawable.ic_cave)
                .into(new AvatarView.ImageViewTarget(imageView));

    }
}
