package com.ltst.schoolapp.parent.ui.school.info.fragment;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.school.SchoolInfoWrapper;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SchoolInfoViewHolder extends BindableViewHolder<SchoolInfoWrapper,
        BindableViewHolder.ActionListener<SchoolInfoWrapper>> {

    @BindView(R.id.school_info_holder_avatar) ImageView avatar;
    @BindView(R.id.school_info_holder_title) TextView titleField;
    @BindView(R.id.school_info_holder_names) TextView namesField;

    public SchoolInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


    @Override public void bindView(int position, SchoolInfoWrapper item, ActionListener<SchoolInfoWrapper> actionListener) {
        super.bindView(position, item, actionListener);
        setAvatar(item.avatarUrl, item.isTeacher);
        setTitle(item.title);
        setNames(item.names, item.isTeacher);

    }

    @BindDimen(R.dimen.avatar_view_avatar_size) int avatarSize;

    private void setAvatar(String avatarUrl, boolean isPerson) {
        Glide.with(avatar.getContext())
                .load(avatarUrl)
                .asBitmap()
                .override(avatarSize, avatarSize)
                .centerCrop()
                .error(isPerson ? R.drawable.ic_profile : R.drawable.ic_cave)
                .into(new AvatarView.ImageViewTarget(avatar));
    }

    private void setTitle(String title) {
        titleField.setText(title);
    }


    @BindString(R.string.teacher) String teacher;
    @BindString(R.string.school) String school;

    private void setNames(List<String> names, boolean isTeacher) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < names.size(); x++) {
            builder.append(names.get(x));
            if (x != names.size() - 1) {
                builder.append(StringUtils.COMMA);
                builder.append(StringUtils.SPACE);
            }
        }
        builder.append(StringUtils.APOSTROPHE_S);
        builder.append(StringUtils.SPACE);
        builder.append(isTeacher ? teacher : school);
        namesField.setText(builder.toString());
    }
}
