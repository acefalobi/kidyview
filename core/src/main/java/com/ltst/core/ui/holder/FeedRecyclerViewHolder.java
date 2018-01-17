package com.ltst.core.ui.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.livetyping.utils.utils.DimenUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.Post;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.adapter.FeedRecyclerAdapter;
import com.ltst.core.util.DateUtils;

public abstract class FeedRecyclerViewHolder extends RecyclerView.ViewHolder {

    private ImageView icon;
    private TextView title;
    private TextView time;
    private TextView likes;
    private ViewGroup share;
    private CardView rootView;
    FrameLayout content;
    private static final String DEFAULT_TITLE = "Your group";
    LayoutInflater inflater;

    public FeedRecyclerViewHolder(View itemView) {
        super(itemView);
        inflater = LayoutInflater.from(itemView.getContext());
        content = (FrameLayout) itemView.findViewById(R.id.feed_item_content);
        icon = ((ImageView) itemView.findViewById(R.id.feed_item_icon));
        title = ((TextView) itemView.findViewById(R.id.feed_item_title));
        time = ((TextView) itemView.findViewById(R.id.feed_item_time));
        likes = ((TextView) itemView.findViewById(R.id.feed_item_likes));
        share = ((ViewGroup) itemView.findViewById(R.id.feed_item_share));
        rootView = ((CardView) itemView.findViewById(R.id.feed_item_root_card_view));
    }

    public void bindView(Post post, FeedRecyclerAdapter.FeedItemListener feedItemListener, boolean itemAsCardView) {
        if (!itemAsCardView) {
            rootView.setBackground(null);
            share.setVisibility(View.GONE);
        }
        String postIconUrl = post.getIconUrl();
        if (postIconUrl != null) {
            if (postIconUrl.equals(Post.CHILD_AVATAR_HOLDER)) {
                iconWithDrawable(R.drawable.ic_child_holder);
            } else if (postIconUrl.equals(Post.GROUP_AVATAR_HOLDER)) {
                iconWithDrawable(R.drawable.ic_cave);
            } else {
                iconWithUrl(postIconUrl);
            }
        }

        String feedTitle = StringUtils.isBlank(post.getTitle()) ?
                title.getContext().getString(R.string.select_person_group_default)
                : post.getTitle();
        title.setText(feedTitle);
        time.setText(DateUtils.getPostTime(time.getContext(), post));
        if (hasShare()) {
            share.setOnClickListener(v -> feedItemListener.onShareClick(post));
        }

    }

    private void iconWithUrl(String uri) {
        int avatarSize = DimenUtils.pxFromDp(itemView.getContext(), 40);
        AvatarView.ImageViewTarget target = new AvatarView.ImageViewTarget(icon);
        Glide.with(itemView.getContext())
                .load(uri)
                .asBitmap()
                .override(avatarSize,avatarSize)
                .centerCrop()
                .into(target);
    }

//    Glide.with(getContext()).load(photoUri)
//    .asBitmap()
//    .override(avatarSize, avatarSize)
//    .centerCrop()
//    .diskCacheStrategy(DiskCacheStrategy.RESULT)
//    .into(imageViewTarget);
//
    private void iconWithDrawable(int drawableResId) {
        Glide.with(itemView.getContext())
                .load(drawableResId)
                .into(icon);
    }

    protected abstract boolean hasShare();


}
