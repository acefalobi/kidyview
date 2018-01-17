package com.ltst.core.ui.holder;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.DimenUtils;
import com.livetyping.utils.utils.SpannableUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.uimodel.SelectPersonModel;

public class SelectPersonViewHolder extends BindableViewHolder<SelectPersonModel,
        SelectPersonViewHolder.SelectPersonActionListener> {

    private ImageView icon;
    private AppCompatCheckedTextView checkedTextView;
    private View rootView;
    private final int avatarSize;

    public SelectPersonViewHolder(View itemView) {
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.fragment_select_person_item_icon);
        checkedTextView = ((AppCompatCheckedTextView) itemView.findViewById(R.id.fragment_select_person_item_text));
        rootView = itemView;
        Context context = getView().getContext();
        int avatarDpSize = (int) context.getResources().getDimension(R.dimen.select_person_icon_size);
        avatarSize = DimenUtils.pxFromDp(context, avatarDpSize);
    }

    public View getView() {
        return rootView;
    }

    @Override
    public void bindView(int position, SelectPersonModel item, SelectPersonActionListener actionListener) {
        String iconUrl = item.getIconUrl();
        if (iconUrl != null && iconUrl.equals(SelectPersonModel.GROUP_HOLDER)) {
            Glide.with(itemView.getContext())
                    .load(R.drawable.ic_cave)
                    .into(icon);
        } else {
            Glide.with(itemView.getContext())
                    .load(iconUrl)
                    .asBitmap()
                    .override(avatarSize, avatarSize)
                    .centerCrop()
                    .thumbnail(0.2f)
                    .error(R.drawable.ic_child_holder)
                    .into(icon);
        }

        SpannableStringBuilder name = new SpannableStringBuilder();
        if (item.getServerId() == SelectPersonModel.GROUP_ID) {
            String yourGroup = itemView.getContext().getString(R.string.select_person_your_group);
            name.append(SpannableUtils.getBoldString(yourGroup))
                    .append(StringUtils.SPACE)
                    .append((item.getName() != null ? item.getName() : ""));
        } else {
            name.append(item.getName());
        }
        checkedTextView.setText(name);
        checkedTextView.setChecked(item.isSelected());
        itemView.setOnClickListener(v -> {
            checkedTextView.setChecked(!checkedTextView.isChecked());
            if (actionListener != null) {
                actionListener.OnItemClickListener(position, item);
            }
        });

    }

    public interface SelectPersonActionListener extends BindableViewHolder.ActionListener<SelectPersonModel> {

    }
}
