package com.ltst.core.ui.holder;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.ui.adapter.dialog.DialogItem;

public class DialogMemberViewHolder extends BindableViewHolder<DialogItem, BindableViewHolder.ActionListener<DialogItem>> {

    private ImageView avatarView;
    private TextView nameField;
    private AppCompatCheckBox checkBox;
    private View rootView;

    public DialogMemberViewHolder(View itemView) {
        super(itemView);
        avatarView = ((ImageView) itemView.findViewById(R.id.viewholder_select_dialog_avatar));
        nameField = ((TextView) itemView.findViewById(R.id.viewholder_select_dialog_name));
        checkBox = ((AppCompatCheckBox) itemView.findViewById(R.id.viewholder_select_dialog_checkbox));
        rootView = itemView;
    }

    public void bindView(int position, DialogItem item, ActionListener<DialogItem> actionListener,
                         boolean showCheckBox) {
        if (!showCheckBox) {
            checkBox.setVisibility(View.GONE);
        }
        bindView(position, item, actionListener);
    }


    @Override public void bindView(int position, DialogItem item, ActionListener<DialogItem> actionListener) {
        super.bindView(position, item, actionListener);
        if (item.isFakeMember()) {
            groupChatItem();
        } else {
            Glide.with(avatarView.getContext())
                    .load(item.getAvatarUrl())
                    .error(R.drawable.ic_child_holder)
                    .into(avatarView);
            nameField.setText(item.getFirstName() + StringUtils.SPACE + item.getLastName());
            rootView.setOnClickListener(v -> actionListener.OnItemClickListener(position, item));
            checkBox.setChecked(item.isChecked());

        }

    }

    private void groupChatItem() {
        Context context = avatarView.getContext();
        Drawable groupIcon = ContextCompat.getDrawable(context, R.drawable.ic_group_black_24dp);
        avatarView.setImageDrawable(groupIcon);
        int color = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        avatarView.getDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        nameField.setText(context.getText(R.string.new_message_fake_name));
    }


}
