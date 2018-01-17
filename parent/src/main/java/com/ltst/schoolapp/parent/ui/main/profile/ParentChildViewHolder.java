package com.ltst.schoolapp.parent.ui.main.profile;


import android.view.View;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.data.model.ParentChild;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParentChildViewHolder
        extends BindableViewHolder<ParentChild, BindableViewHolder.ActionListener<ParentChild>> {


    @BindView(R.id.parent_child_viewholder_avatar) AvatarView avatarView;
    @BindView(R.id.parent_child_viewholder_name_filed) TextView firstNameField;
    @BindView(R.id.parent_child_viewholder_last_name_filed) TextView lastNameField;
    @BindView(R.id.parent_child_viewholder_school_filed) TextView schoolField;
    @BindView(R.id.children_viewholder_missed) TextView notProvidedField;

    public ParentChildViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void bindView(int position, ParentChild item, ActionListener<ParentChild> actionListener) {
        super.bindView(position, item, actionListener);
        Child child = item.getChild();
        avatarView.setAvatar(child.getAvatarUrl());
        firstNameField.setText(child.getFirstName());
        lastNameField.setText(child.getLastName());
        schoolField.setText(item.getSchoolTitle());
        if (StringUtils.isBlank(child.getAllergies())
                || StringUtils.isBlank(child.getBloodGroup())
                || StringUtils.isBlank(child.getGenotype())) {
            notProvidedField.setVisibility(View.VISIBLE);
        } else notProvidedField.setVisibility(View.GONE);
    }
}
