package com.ltst.schoolapp.parent.ui.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.livetyping.utils.utils.DimenUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.ChildInGroup;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class ChildInGroupAdapter extends BaseAdapter implements SpinnerAdapter {

    private final static int COUNT_OF_TITLE_CHARS = 15;


    private List<ChildInGroup> childrenInGroups;
    private WeakReference<Spinner> spinnerWeakReference;

    public ChildInGroupAdapter(WeakReference<Spinner> spinnerWeakReference) {
        this.spinnerWeakReference = spinnerWeakReference;
    }

    @Override public int getCount() {
        return childrenInGroups.size();
    }

    @Override public Object getItem(int position) {
        return childrenInGroups.get(position);
    }

    @Override public long getItemId(int position) {
        return childrenInGroups.get(position).getChildId();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(spinnerWeakReference.get().getContext());
        View resultView;
        if (convertView == null) {
            resultView = layoutInflater.inflate(R.layout.spiner_item, parent, false);
        } else resultView = convertView;
        TextView groupTitle = (TextView) resultView.findViewById(R.id.spinner_item_title);
        ChildInGroup childInGroup = childrenInGroups.get(position);
        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(childInGroup.getFirtName())
                .append(StringUtils.SPACE)
                .append(StringUtils.DASH)
                .append(StringUtils.SPACE)
                .append(childInGroup.getGroupTitle());
        String text = StringUtils.cutLongText(titleBuilder.toString(), COUNT_OF_TITLE_CHARS);
        groupTitle.setText(text);
        return resultView;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(spinnerWeakReference.get().getContext());
        View view = layoutInflater.inflate(R.layout.layout_child_in_group_dropdown_item, parent, false);
        ImageView groupAvatar =
                (ImageView) view.findViewById(R.id.spinner_item_group_icon);
        ImageView childAvatar =
                ((ImageView) view.findViewById(R.id.spinner_item_child_avatar_icon));
        TextView itemTextView = ((TextView) view.findViewById(R.id.spinner_item_text));

        ChildInGroup childInGroup = childrenInGroups.get(position);

//        int avatarDimenSize = (int) spinnerWeakReference.get().getResources()
//                .getDimension(R.dimen.spinner_dropdown_avatar_size);

        int avatarSize = DimenUtils.pxFromDp(parent.getContext(), 32);

        AvatarView.ImageViewTarget groupAvatarViewTarget = new AvatarView.ImageViewTarget(groupAvatar);
        Glide.with(spinnerWeakReference.get().getContext())
                .load(childInGroup.getGroupAvatarUrl())
                .asBitmap()
                .error(R.drawable.ic_cave)
                .override(avatarSize, avatarSize)
                .centerCrop()
                .into(groupAvatarViewTarget);

        AvatarView.ImageViewTarget childAvatarViewTarget = new AvatarView.ImageViewTarget(childAvatar);
        Glide.with(spinnerWeakReference.get().getContext())
                .load(childInGroup.getChildAvatarUrl())
                .asBitmap()
                .error(R.drawable.ic_child_holder)
                .override(avatarSize, avatarSize)
                .centerCrop()
                .into(childAvatarViewTarget);

        StringBuilder builder = new StringBuilder()
                .append(childInGroup.getFirtName())
                .append(StringUtils.SPACE)
                .append(StringUtils.DASH)
                .append(StringUtils.SPACE)
                .append(childInGroup.getGroupTitle());
        itemTextView.setText(StringUtils.cutLongText(builder.toString(), COUNT_OF_TITLE_CHARS));
        return view;
    }


    public void addAll(List<ChildInGroup> childInGroups) {
        this.childrenInGroups = childInGroups;
    }
}
