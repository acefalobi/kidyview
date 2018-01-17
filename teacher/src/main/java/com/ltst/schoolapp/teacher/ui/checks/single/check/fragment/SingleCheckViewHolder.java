package com.ltst.schoolapp.teacher.ui.checks.single.check.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.model.ChildStateType;
import com.ltst.core.ui.holder.ChecksViewHolder;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Danil on 24.09.2016.
 */

public class SingleCheckViewHolder extends BindableViewHolder<ChildCheck,
        SingleCheckViewHolder.SelectPersonActionListener> {

    @BindView(R.id.fragment_single_check_item_photo)
    ImageView photo;
    @BindView(R.id.fragment_single_check_item_text)
    TextView name;
    @BindView(R.id.fragment_single_check_item_states_container)
    LinearLayout statesContainer;

    public SingleCheckViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindView(int position, ChildCheck item, SelectPersonActionListener actionListener) {
        Glide.with(itemView.getContext())
                .load(item.getChild().getAvatarUrl())
                .thumbnail(0.2f)
                .error(R.drawable.ic_profile)
                .into(photo);
        name.setText(item.getChild().getLastName() + StringUtils.SPACE + item.getChild().getFirstName());
        statesContainer.removeAllViews();
        for (ChildState state : item.getChildStates()) {
            addStateView(state);
        }
    }

    private void addStateView(ChildState state) {
        LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
        View stateView = inflater.inflate(R.layout.fragment_check_item_state, statesContainer, false);
        ImageView stateIcon = (ImageView) stateView.findViewById(R.id.fragment_check_item_state_image);
        TextView stateText = (TextView) stateView.findViewById(R.id.fragment_check_item_state_text);
        TextView stateTime = (TextView) stateView.findViewById(R.id.fragment_check_item_state_time);
        if (state.getType().equals(ChildStateType.CHECKIN)) {
            stateIcon.setImageResource(R.drawable.ic_arrow_checkin);
        } else if (state.getType().equals(ChildStateType.CHECKOUT)) {
            stateIcon.setImageResource(R.drawable.ic_arrow_checkout);
        } else {
            stateIcon.setImageDrawable(null);
        }
        stateText.setText(ChecksViewHolder.getInfoText(state, stateText.getContext()));
        stateTime.setText(DateUtils.getHourMinuteString(state.getDatetime(), itemView.getContext()));

        statesContainer.addView(stateView);
    }

    public interface SelectPersonActionListener extends BindableViewHolder
            .ActionListener<ChildCheck> {

    }
}
