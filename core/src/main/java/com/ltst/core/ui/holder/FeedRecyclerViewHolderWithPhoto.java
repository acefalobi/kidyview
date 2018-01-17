package com.ltst.core.ui.holder;

import android.graphics.Typeface;
import android.support.percent.PercentFrameLayout.LayoutParams;
import android.support.percent.PercentLayoutHelper.PercentLayoutInfo;
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
import com.ltst.core.data.model.Post;
import com.ltst.core.ui.adapter.FeedRecyclerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Danil on 22.09.2016.
 */

public class FeedRecyclerViewHolderWithPhoto extends FeedRecyclerViewHolder {
    private ImageView firstPhoto;
    private ImageView secondPhoto;
    private ImageView thirdPhoto;
    private TextView photoText;

    public FeedRecyclerViewHolderWithPhoto(View itemView, int count) {
        super(itemView);
        inflater.inflate(R.layout.viewholder_feed_item_photo, content, true);
        firstPhoto = ((ImageView) itemView.findViewById(R.id.feed_item_photo_first));
        secondPhoto = ((ImageView) itemView.findViewById(R.id.feed_item_photo_second));
        thirdPhoto = ((ImageView) itemView.findViewById(R.id.feed_item_photo_third));
        photoText = ((TextView) itemView.findViewById(R.id.feed_item_photo_text));
        switch (count) {
            case 1:
                getLayoutInfo(firstPhoto).widthPercent = 1f;
                getLayoutInfo(firstPhoto).heightPercent = 1f;
                break;
            case 2:
                getLayoutInfo(firstPhoto).widthPercent = 0.5f;
                getLayoutInfo(firstPhoto).heightPercent = 1f;
                getLayoutInfo(secondPhoto).widthPercent = 0.5f;
                getLayoutInfo(secondPhoto).heightPercent = 1f;
                getLayoutInfo(secondPhoto).leftMarginPercent = 0.5f;
                break;
            case 3:
                getLayoutInfo(firstPhoto).widthPercent = 0.5f;
                getLayoutInfo(firstPhoto).heightPercent = 1f;
                getLayoutInfo(secondPhoto).widthPercent = 0.5f;
                getLayoutInfo(secondPhoto).heightPercent = 0.5f;
                getLayoutInfo(secondPhoto).leftMarginPercent = 0.5f;
                getLayoutInfo(thirdPhoto).widthPercent = 0.5f;
                getLayoutInfo(thirdPhoto).heightPercent = 0.5f;
                getLayoutInfo(thirdPhoto).leftMarginPercent = 0.5f;
                getLayoutInfo(thirdPhoto).topMarginPercent = 0.5f;
                break;
        }
    }

    @Override
    public void bindView(Post post, FeedRecyclerAdapter.FeedItemListener feedItemListener, boolean itemAsCardView) {
        super.bindView(post, feedItemListener, itemAsCardView);
        int count = post.getImages().size();
        count = count > 3 ? 3 : count;
        List<ImageView> imageViews = Arrays.asList(firstPhoto, secondPhoto, thirdPhoto);
        for (int i = 0; i < count; i++) {
            ImageView imageView = imageViews.get(i);
            String url = post.getImages().get(i).getUrl();
            Glide.with(itemView.getContext())
                    .load(url)
                    .thumbnail(0.3f)
                    .centerCrop()
                    .into(imageView);
            imageView.setOnClickListener(v ->
                    feedItemListener.onPhotoClick(url, imageView));
        }
        String activityTitle = post.getActivity().getTitle();
        String content = post.getContent();
        if (StringUtils.isBlank(content)) {
            activityTitle = activityTitle + StringUtils.DOT;
        } else {
            activityTitle = activityTitle + StringUtils.COLON;
        }
        String text = StringUtils.capitalize(activityTitle)
                + StringUtils.SPACE
                + post.getContent();
        Spannable span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.BOLD), 0, activityTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        photoText.setText(span);
    }

    @Override protected boolean hasShare() {
        return true;
    }

    private PercentLayoutInfo getLayoutInfo(View view) {
        return ((LayoutParams) view.getLayoutParams()).getPercentLayoutInfo();
    }


}
