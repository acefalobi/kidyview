package com.ltst.schoolapp.parent.ui.child.view;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.core.data.model.Group;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildGroupViewHolder extends BindableViewHolder<Group, BindableViewHolder.ActionListener<Group>> {

    @BindView(R.id.view_child_item_group_avatar) ImageView groupAvatar;
    @BindView(R.id.view_child_item_group_title) TextView groupTitle;
    @BindDimen(R.dimen.view_child_item_group_avatar_size) int avatarSize;
    @BindDimen(R.dimen.activity_horizontal_margin_14) int itemGroupMargin;

    public ChildGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void bindView(int position, Group item, ActionListener<Group> actionListener) {
        super.bindView(position, item, actionListener);
        AvatarView.ImageViewTarget imageTarget = new AvatarView.ImageViewTarget(groupAvatar);
        Glide.with(groupAvatar.getContext())
                    .load(item.getAvatarUrl())
                    .asBitmap()
                    .override(avatarSize, avatarSize)
                    .centerCrop()
                    .placeholder(R.drawable.ic_cave)
                    .error(R.drawable.ic_cave)
                    .into(imageTarget);
            groupTitle.setText(item.getTitle());
//                int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
//                LinearLayout.LayoutParams marginParams =
//                        new LinearLayout.LayoutParams(wrapContent, wrapContent);
//                marginParams.setMargins(itemGroupMargin,0,0,0);
//            childGroupsContainer.addView(itemView,marginParams);
    }
}
