package com.ltst.schoolapp.parent.ui.main.checks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.DimenUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.model.ChildStateType;
import com.ltst.core.ui.holder.ChecksViewHolder;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;

public class ParentChecksViewHolder extends BindableViewHolder<ChecksAdapter.ChecksWrapper,
        BindableViewHolder.ActionListener<ChecksAdapter.ChecksWrapper>> {

    private ImageView photo;
    private TextView name;
    private LinearLayout statesContainer;
    private View topDivider;
    private View bottomDivider;
    private View topShortDivider;
    private View dateDivider;
    private TextView dateField;
    private int avatarSize;

    public ParentChecksViewHolder(View itemView) {
        super(itemView);
        photo = ((ImageView) itemView.findViewById(R.id.fragment_checks_item_photo));
        name = ((TextView) itemView.findViewById(R.id.fragment_checks_item_text));
        statesContainer = ((LinearLayout) itemView.findViewById(R.id.fragment_checks_item_states_container));
        topDivider = (itemView.findViewById(R.id.fragment_checks_item_top_divider));
        bottomDivider = itemView.findViewById(R.id.fragment_checks_item_bottom_divider);
        topShortDivider = itemView.findViewById(R.id.fragment_checks_item_top_short_divider);
        dateDivider = itemView.findViewById(R.id.fragment_checks_item_date_separator);
        dateField = ((TextView) itemView.findViewById(R.id.fragment_checks_item_date_field));
        Context context = photo.getContext();
        int avatarDpSize = (int) context.getResources().getDimension(com.ltst.core.R.dimen.select_person_icon_size);
        avatarSize = DimenUtils.pxFromDp(context, avatarDpSize);
    }

    public void bindView(int position, ChecksAdapter.ChecksWrapper checksWrapper, boolean showBottomDivider) {
        ChildCheck item = checksWrapper.getChildCheck();
        super.bindView(position, checksWrapper, null);
        Glide.with(itemView.getContext())
                .load(item.getChild().getAvatarUrl())
                .asBitmap()
                .override(avatarSize,avatarSize)
                .centerCrop()
                .thumbnail(0.2f)
                .error(R.drawable.ic_profile)
                .into(photo);
        name.setText(item.getChild().getFirstName() +
                StringUtils.SPACE +
                item.getChild().getLastName());

        prepareStateContainer(item);
        for (int index = 0; index < item.getChildStates().size(); index++) {
            addStateView(index, item.getChildStates().get(index));
        }
        String date = checksWrapper.getDate();
        if (!StringUtils.isBlank(date)) {
            dateField.setVisibility(View.VISIBLE);
            dateField.setText(date);
            dateDivider.setVisibility(View.GONE);
//            bottomDivider.setVisibility(View.GONE);
            topShortDivider.setVisibility(View.GONE);
            topDivider.setVisibility(View.VISIBLE);
        } else {
            topShortDivider.setVisibility(View.VISIBLE);
            topDivider.setVisibility(View.GONE);
            dateField.setVisibility(View.GONE);
            dateDivider.setVisibility(View.GONE);
//            bottomDivider.setVisibility(View.VISIBLE);
        }
        bottomDivider.setVisibility(showBottomDivider ? View.VISIBLE : View.GONE);
        topShortDivider.setVisibility(showBottomDivider ? View.VISIBLE : View.GONE);
    }

    private void prepareStateContainer(ChildCheck item) {
        statesContainer.removeAllViews();
        for (int i = 0; i < item.getChildStates().size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            inflater.inflate(R.layout.fragment_check_item_state, statesContainer, true);
        }
    }

    private void addStateView(int index, ChildState state) {
        View stateView = statesContainer.getChildAt(index);
        ImageView stateIcon = (ImageView) stateView.findViewById(R.id.fragment_check_item_state_image);
        TextView stateText = (TextView) stateView.findViewById(R.id.fragment_check_item_state_text);
        TextView stateTime = (TextView) stateView.findViewById(R.id.fragment_check_item_state_time);
        if (state.getType() == ChildStateType.CHECKIN) {
            stateIcon.setImageResource(R.drawable.ic_arrow_checkin);
        } else if (state.getType() == ChildStateType.CHECKOUT) {
            stateIcon.setImageResource(R.drawable.ic_arrow_checkout);
        } else {
            stateIcon.setImageDrawable(null);
        }
        stateText.setText(ChecksViewHolder.getInfoText(state, stateText.getContext()));
        stateTime.setText(DateUtils.getHourMinuteString(state.getDatetime(), itemView.getContext()));

    }


}
