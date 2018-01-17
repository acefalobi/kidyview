package com.ltst.core.ui.adapter;

import android.view.View;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.R;
import com.ltst.core.data.model.Post;
import com.ltst.core.ui.holder.FeedCheckoutReportViewHolder;
import com.ltst.core.ui.holder.FeedRecyclerViewHolder;
import com.ltst.core.ui.holder.FeedRecyclerViewHolderActivity;
import com.ltst.core.ui.holder.FeedRecyclerViewHolderWithPhoto;


public class FeedRecyclerAdapter extends RecyclerBindableAdapter<Post, FeedRecyclerViewHolder> {

    private FeedItemListener feedItemListener;
    private boolean itemAsCardView;

    public FeedRecyclerAdapter setFeedItemListener(FeedItemListener feedItemListener) {
        this.feedItemListener = feedItemListener;
        return this;
    }

    public FeedRecyclerAdapter(boolean itemAsCardView) {
        this.itemAsCardView = itemAsCardView;
    }

    @Override
    protected void onBindItemViewHolder(FeedRecyclerViewHolder viewHolder, int position, int type) {
        viewHolder.bindView(getItem(position), feedItemListener, itemAsCardView);
    }

    @Override
    protected FeedRecyclerViewHolder viewHolder(View view, int type) {
        switch (type) {
            case Post.CHECKOUT_REPORT:
                return new FeedCheckoutReportViewHolder(view);
            case Post.WITH_ONE_PHOTO:
                return new FeedRecyclerViewHolderWithPhoto(view, 1);
            case Post.WITH_TWO_PHOTO:
                return new FeedRecyclerViewHolderWithPhoto(view, 2);
            case Post.WITH_TREE_PHOTO:
                return new FeedRecyclerViewHolderWithPhoto(view, 3);
            default:
            case Post.WITH_ACTIVITY:
                return new FeedRecyclerViewHolderActivity(view);
        }
    }

    @Override
    protected int layoutId(int type) {
        if (type == Post.CHECKOUT_REPORT) {
            return R.layout.viewholder_feed_checkout_item;
        }
        return R.layout.viewholder_feed_item;
    }

    @Override
    protected int getItemType(int position) {
        if (getHeadersCount() == 1) {
            return getItem(position - 1).getListType();
        }
        else return getItem(position).getListType();

    }

    public interface FeedItemListener {
        void onPhotoClick(String url, View photo);

        void onShareClick(Post post);

        void onIconClick(Post post);

        void onReportClick(Post post);
    }
}
