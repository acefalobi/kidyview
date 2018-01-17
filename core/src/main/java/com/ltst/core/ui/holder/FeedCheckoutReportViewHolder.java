package com.ltst.core.ui.holder;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.livetyping.utils.utils.DimenUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.Post;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.adapter.FeedRecyclerAdapter;
import com.ltst.core.util.DateUtils;

public class FeedCheckoutReportViewHolder extends FeedRecyclerViewHolder {

    private ImageView checkoutAvatar;
    private TextView checkoutName;
    private TextView checkoutTime;
    private ViewGroup rootView;

    public FeedCheckoutReportViewHolder(View itemView) {
        super(itemView);
        checkoutAvatar = ((ImageView) itemView.findViewById(R.id.checkout_report_icon));
        checkoutName = ((TextView) itemView.findViewById(R.id.checkout_report_name));
        checkoutTime = ((TextView) itemView.findViewById(R.id.checkout_report_time));
        rootView = ((ViewGroup) itemView.findViewById(R.id.checkout_report_root_view));
    }

    public void bindView(Post post, FeedRecyclerAdapter.FeedItemListener feedItemListener, boolean itemAsCardView) {
        checkoutName.setText(post.getChildFirstName() + StringUtils.SPACE + post.getChildLastName());
        int avatarSize = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.select_person_icon_size);
        AvatarView.ImageViewTarget target = new AvatarView.ImageViewTarget(checkoutAvatar);
        Glide.with(itemView.getContext())
                .load(post.getChildAvatarUrl())
                .asBitmap()
                .error(R.drawable.ic_child_holder)
                .override(avatarSize, avatarSize)
                .centerCrop()
                .into(target);

        checkoutTime.setText(DateUtils.getPostTime(checkoutTime.getContext(), post));
        rootView.setOnClickListener(v -> feedItemListener.onReportClick(post));
    }

    @Override protected boolean hasShare() {
        return false;
    }

}
