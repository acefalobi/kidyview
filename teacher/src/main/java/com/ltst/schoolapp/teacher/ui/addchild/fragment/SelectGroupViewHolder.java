package com.ltst.schoolapp.teacher.ui.addchild.fragment;


import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Group;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectGroupViewHolder extends BindableViewHolder<SelectableGroup, BindableViewHolder.ActionListener<SelectableGroup>> {

    @BindView(R.id.select_group_item_checkbox) AppCompatCheckBox checkBox;
    @BindView(R.id.select_group_item_image) ImageView imageView;
    @BindView(R.id.select_group_item_text) TextView title;
    @BindView(R.id.select_group_item_root) ViewGroup rootView;

    public SelectGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @BindDimen(R.dimen.add_child_group_item_avatar_size) int avatarSize;
    private static final int MAX_TITLE_LENGHT = 15;

    @Override public void bindView(int position, SelectableGroup item, ActionListener<SelectableGroup> actionListener) {
        super.bindView(position, item, actionListener);
        checkBox.setChecked(item.isSelected());
//        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> rootView.performClick());
        checkBox.setClickable(false);
        rootView.setOnClickListener(v -> {
            checkBox.setChecked(!checkBox.isChecked());
            item.setSelected(!item.isSelected());
            actionListener.OnItemClickListener(position, item);

        });
        Group group = item.getGroup();
        String title = group.getTitle();
        if (title.length() > MAX_TITLE_LENGHT) {
            title = title.substring(0, MAX_TITLE_LENGHT) + StringUtils.THREEDOTS;
        }
        this.title.setText(title);
        Glide.with(imageView.getContext())
                .load(group.getAvatarUrl())
                .asBitmap()
                .override(avatarSize, avatarSize)
                .centerCrop()
                .error(R.drawable.ic_cave)
                .into(new AvatarView.ImageViewTarget(imageView));

    }

    public interface SelectGroupEventListener extends BindableViewHolder.ActionListener<SelectableGroup> {
        void onGroupSelected(SelectableGroup group, boolean isSelected);
    }

}



