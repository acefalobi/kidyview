package com.ltst.core.ui.holder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.Member;
import com.ltst.core.ui.AvatarView;

public class FamilyViewHolder extends BindableViewHolder<Member, FamilyViewHolder.MemberClickListener> {

    private AvatarView avatarView;
    private TextView firstNameField;
    private TextView lastNameField;
    private TextView positionField;
    private TextView emailField;
    private TextView phoneField;
    private TextView accessLevelField;
    private ViewGroup emailContainer;
    private ViewGroup phoneContainer;

    public FamilyViewHolder(View itemView) {
        super(itemView);
        avatarView = ((AvatarView) itemView.findViewById(R.id.member_card_avatar));
        firstNameField = ((TextView) itemView.findViewById(R.id.member_card_first_name));
        lastNameField = ((TextView) itemView.findViewById(R.id.member_card_last_name));
        positionField = ((TextView) itemView.findViewById(R.id.member_card_position));
        emailField = ((TextView) itemView.findViewById(R.id.member_card_email));
        phoneField = ((TextView) itemView.findViewById(R.id.member_card_phone));
        accessLevelField = ((TextView) itemView.findViewById(R.id.member_card_status));
        emailContainer = ((ViewGroup) itemView.findViewById(R.id.member_card_email_container));
        phoneContainer = ((ViewGroup) itemView.findViewById(R.id.member_card_phone_container));

    }

    @Override
    public void bindView(int position, Member item, MemberClickListener actionListener) {
        String avatarUrl = item.getAvatarUrl();
//        avatarView.setAvatar(avatarUrl != null ? avatarUrl : null);
        if (!StringUtils.isBlank(avatarUrl)) {
            avatarView.setAvatar(avatarUrl);
        } else {
            avatarView.clearAvatar();
        }
        firstNameField.setText(item.getFirstName());
        lastNameField.setText(item.getLastName());
        positionField.setText(item.getPosition());
        emailField.setText(item.getEmail());
        phoneField.setText(item.getPhone());
        Context context = accessLevelField.getContext();
        String accessLevel = item.getAccessLevel();
        accessLevelField.setText(accessLevel);
        if (accessLevel.equals(Member.LIMITED_ACCESS)) {
            accessLevelField.setBackground(ContextCompat.getDrawable(context,
                    R.drawable.member_status_background_blue));
        } else if (accessLevel.equals(Member.FULL_ACCESS)) {
            accessLevelField.setBackground(ContextCompat.getDrawable(context,
                    R.drawable.member_status_background_green));
        }
        phoneContainer.setOnClickListener(view -> actionListener.onPhoneClick(item));
        emailContainer.setOnClickListener(view -> actionListener.onEmailClick(item));
        avatarView.setClickAvatarCallBack(() -> actionListener.OnItemClickListener(position, item));
    }


    public void bindViewWithAccess(int position, Member item, MemberClickListener actionListener,
                                   boolean canChangeStatus) {
        if (canChangeStatus) {
            accessLevelField.setClickable(true);
            accessLevelField.setOnClickListener(v -> actionListener.onChangeStatusClick(item));
        } else {
            accessLevelField.setClickable(false);
        }
        bindView(position, item, actionListener);
    }

    public interface MemberClickListener extends BindableViewHolder.ActionListener<Member> {
        void onPhoneClick(Member item);

        void onEmailClick(Member item);

        void onChangeStatusClick(Member item);

    }

}
