package com.ltst.schoolapp.teacher.ui.activities.add.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.schoolapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Danil on 26.09.2016.
 */

public class ChildActivityViewHolder extends BindableViewHolder<ChildActivity,
        ChildActivityViewHolder.ChildActivityListener> {

    @BindView(R.id.add_post_activity_text)
    TextView text;
    @BindView(R.id.add_post_activity_image)
    ImageView imageView;

    public ChildActivityViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindView(int position, ChildActivity item, ChildActivityListener actionListener) {
        super.bindView(position, item, actionListener);
        text.setText(StringUtils.capitalize(item.getTitle()));
        Glide.with(itemView.getContext())
                .load(item.getIconUrl())
                .thumbnail(0.3f)
                .into(imageView);
    }

    public interface ChildActivityListener extends BindableViewHolder.ActionListener<ChildActivity> {
    }
}
