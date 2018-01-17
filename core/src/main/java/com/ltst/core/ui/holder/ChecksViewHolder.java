package com.ltst.core.ui.holder;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.DimenUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.model.ChildStateType;
import com.ltst.core.data.realm.model.ChildCheckScheme;
import com.ltst.core.data.realm.model.ChildStateScheme;
import com.ltst.core.util.DateUtils;

import io.realm.RealmViewHolder;


public class ChecksViewHolder extends RealmViewHolder {

    private ImageView photo;
    private TextView name;
    private LinearLayout statesContainer;
    private View topDivider;
    private View bottomDivider;
    private View bottomShortDivider;
    private int avatarSize;

    public ChecksViewHolder(View itemView) {
        super(itemView);
        photo = ((ImageView) itemView.findViewById(R.id.fragment_checks_item_photo));
        name = ((TextView) itemView.findViewById(R.id.fragment_checks_item_text));
        statesContainer = ((LinearLayout) itemView.findViewById(R.id.fragment_checks_item_states_container));
        topDivider = (itemView.findViewById(R.id.fragment_checks_item_top_divider));
        bottomDivider = itemView.findViewById(R.id.fragment_checks_item_bottom_divider);
        bottomShortDivider = itemView.findViewById(R.id.fragment_checks_item_bottom_short_divider);
        Context context = photo.getContext();
        int avatarDpSize = (int) context.getResources().getDimension(R.dimen.select_person_icon_size);
        avatarSize = DimenUtils.pxFromDp(context, avatarDpSize);
    }

    public void bindView(ChildCheckScheme item, SelectPersonActionListener actionListener,
                         boolean previousIsHeader, boolean nextIsHeader) {
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
                item.getChild().getLastName()
        );

        prepareStateContainer(item);
        for (int i = 0; i < item.getChildStates().size(); i++) {
            addStateView(i, item.getChildStates().get(i));
        }
        topDivider.setVisibility(previousIsHeader ? View.VISIBLE : View.GONE);
        bottomDivider.setVisibility(nextIsHeader ? View.VISIBLE : View.GONE);
        bottomShortDivider.setVisibility(!nextIsHeader ? View.VISIBLE : View.GONE);
    }

    private void prepareStateContainer(ChildCheckScheme item) {
        statesContainer.removeAllViews();
        for (int i = 0; i < item.getChildStates().size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            inflater.inflate(R.layout.fragment_check_item_state, statesContainer, true);
        }
    }

    private void addStateView(int i, ChildStateScheme state) {
        View stateView = statesContainer.getChildAt(i);
        ImageView stateIcon = (ImageView) stateView.findViewById(R.id.fragment_check_item_state_image);
        TextView stateText = (TextView) stateView.findViewById(R.id.fragment_check_item_state_text);
        TextView stateTime = (TextView) stateView.findViewById(R.id.fragment_check_item_state_time);
        Spannable text = getInfoText(ChildState.fromScheme(state), stateText.getContext());
        if (state.getType().equals(ChildStateType.CHECKIN.toString())) {
            stateIcon.setImageResource(R.drawable.ic_arrow_checkin);
        } else if (state.getType().equals(ChildStateType.CHECKOUT.toString())) {
            stateIcon.setImageResource(R.drawable.ic_arrow_checkout);
        } else {
            stateIcon.setImageDrawable(null);
        }
        stateText.setText(text);
        stateTime.setText(DateUtils.getHourMinuteString(state.getDatetime(), itemView.getContext()));

    }

    public static Spannable getInfoText(ChildState state, Context context) {
        String type = state.getType().toString();
        String format = context.getString(type.equals(ChildStateType.CHECKIN.toString())
                ? R.string.checks_dropped_of_format
                : R.string.checks_picked_by_format);
        String visibleName = state.getFirstName() + StringUtils.SPACE + state.getLastName();
        String text = String.format(format, visibleName);
        Spannable result = new SpannableString(text);
        result.setSpan(new StyleSpan(Typeface.BOLD), text.length() - visibleName.length(), text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return result;
    }

    public interface SelectPersonActionListener extends BindableViewHolder
            .ActionListener<ChildCheckScheme> {

    }
}
