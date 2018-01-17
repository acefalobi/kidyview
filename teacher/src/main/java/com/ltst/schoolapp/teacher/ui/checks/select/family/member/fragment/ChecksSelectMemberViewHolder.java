package com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.core.data.uimodel.ChecksSelectMemberModel;
import com.ltst.schoolapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChecksSelectMemberViewHolder extends BindableViewHolder<ChecksSelectMemberModel,
        ChecksSelectMemberViewHolder.SelectPersonActionListener> {

    @BindView(R.id.fragment_checks_select_member_item_icon)
    ImageView icon;
    @BindView(R.id.fragment_checks_select_member_item_text)
    TextView checkedTextView;
    @BindView(R.id.fragment_checks_select_member_item_radio)
    RadioButton radioButton;
    private final int avatarSize;

    public ChecksSelectMemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        avatarSize = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.select_person_icon_size);
    }

    @Override
    public void bindView(int position, ChecksSelectMemberModel item, SelectPersonActionListener actionListener) {
        Glide.with(itemView.getContext())
                .load(item.getIconUrl())
                .asBitmap()
                .centerCrop()
                .override(avatarSize, avatarSize)
                .thumbnail(0.2f)
                .error(R.drawable.ic_profile)
                .into(icon);
        String name;
        if (item.getMemberId() == ChecksSelectMemberModel.OTHER_ID) {
            name = itemView.getContext().getString(R.string.checks_select_member_other);
        } else {
            name = item.getName();
        }
        checkedTextView.setText(name);
        radioButton.setChecked(item.isSelected());
        itemView.setOnClickListener(v -> {
            radioButton.setChecked(!radioButton.isChecked());
            if (actionListener != null) {
                actionListener.OnItemClickListener(position, item);
            }
        });
    }

    public interface SelectPersonActionListener extends
            BindableViewHolder.ActionListener<ChecksSelectMemberModel> {

    }
}
