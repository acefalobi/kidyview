package com.ltst.schoolapp.parent.ui.checkout.select.school.fragment;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.data.model.ChildInGroupInSchool;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildInGroupViewHolder extends BindableViewHolder<ChildInGroupInSchool,
        BindableViewHolder.ActionListener<ChildInGroupInSchool>> {

    @BindView(R.id.object_root_view) ViewGroup rootView;
    @BindView(R.id.object_item_group_icon) ImageView groupAvatar;
    @BindView(R.id.object_item_child_avatar_icon) ImageView childAvatar;
    @BindView(R.id.object_item_text) TextView textView;
    @BindView(R.id.object_item_radioButton) RadioButton radioButton;

    public ChildInGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void bindView(int position, ChildInGroupInSchool item, ActionListener<ChildInGroupInSchool> actionListener) {
        radioButton.setChecked(item.isSelected());
        radioButton.setClickable(false);
        textView.setText(ChildInGroupInSchool.getObjectTitle(item));

        int avatarSize = (int) groupAvatar.getContext().getResources()
                .getDimension(R.dimen.spinner_dropdown_avatar_size);

        AvatarView.ImageViewTarget groupAvatarViewTarget = new AvatarView.ImageViewTarget(groupAvatar);
        Glide.with(groupAvatar.getContext())
                .load(item.getAvatarUrl())
                .asBitmap()
                .override(avatarSize, avatarSize)
                .error(R.drawable.ic_cave)
                .centerCrop()
                .into(groupAvatarViewTarget);

        AvatarView.ImageViewTarget childAvatarViewTarget = new AvatarView.ImageViewTarget(childAvatar);
        Glide.with(childAvatar.getContext())
                .load(item.getChild().getAvatarUrl())
                .asBitmap()
                .override(avatarSize, avatarSize)
                .error(R.drawable.ic_child_holder)
                .centerCrop()
                .into(childAvatarViewTarget);

        rootView.setOnClickListener(v -> actionListener.OnItemClickListener(position, item));
    }
}
