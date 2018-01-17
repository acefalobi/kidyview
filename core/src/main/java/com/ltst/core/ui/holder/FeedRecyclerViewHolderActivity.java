package com.ltst.core.ui.holder;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.data.model.Post;
import com.ltst.core.ui.adapter.FeedRecyclerAdapter;

import butterknife.ButterKnife;

/**
 * Created by Danil on 22.09.2016.
 */

public class FeedRecyclerViewHolderActivity extends FeedRecyclerViewHolder {

    private ImageView image;
    private TextView textView;

    public FeedRecyclerViewHolderActivity(View itemView) {
        super(itemView);
        inflater.inflate(R.layout.viewholder_feed_item_activity, content, true);
        image = ((ImageView) itemView.findViewById(R.id.feed_item_activity_image));
        textView = ((TextView) itemView.findViewById(R.id.feed_item_activity_text));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindView(Post post, FeedRecyclerAdapter.FeedItemListener feedItemListener, boolean itemAsCardView) {
        super.bindView(post, feedItemListener, itemAsCardView);
        String content = post.getContent();
        ChildActivity activity = post.getActivity();
        if (activity != null) {
            String title = activity.getTitle();
            if (!StringUtils.isBlank(content)) {
                title = title + StringUtils.COLON;
            } else {
                title = title + StringUtils.DOT;
            }
            String resultText = StringUtils.capitalize(title)
                    + StringUtils.SPACE
                    + content;
            Spannable span = new SpannableString(resultText);
            span.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(span);
            Glide.with(itemView.getContext())
                    .load(post.getActivity().getIconUrl())
                    .thumbnail(0.5f)
                    .into(image);
        }


    }

    @Override protected boolean hasShare() {
        return true;
    }
}
