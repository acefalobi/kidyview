package com.ltst.core.ui.holder;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.EmptyEvent;
import com.ltst.core.data.model.Event;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.util.DateUtils;

import java.util.List;

public class EventsViewHolder extends BindableViewHolder<Event, EventsViewHolder.EventsClickListener> {

    private TextView eventText;
    private TextView eventTime;
    private ImageView eventImage;
    private Button docButton;
    private ViewGroup docContainer;
    private View separator;
    private ViewGroup emptyListIndicator;
    private TextView groupTitle;
    private ImageView groupAvatar;
    private TextView empyEventsField;

    public EventsViewHolder(View itemView) {
        super(itemView);
        eventText = ((TextView) itemView.findViewById(R.id.event_viewholder_text));
        eventTime = ((TextView) itemView.findViewById(R.id.event_viewholder_time));
        eventImage = ((ImageView) itemView.findViewById(R.id.event_viewholder_image));
        docButton = ((Button) itemView.findViewById(R.id.event_viewholder_doc_button));
        docContainer = ((ViewGroup) itemView.findViewById(R.id.event_viewholder_doc_container));
        separator = itemView.findViewById(R.id.event_viewholder_separator);
        emptyListIndicator = ((ViewGroup) itemView.findViewById(R.id.event_viewholder_empty_list_indicator));
        groupTitle = ((TextView) itemView.findViewById(R.id.event_viewholder_group_title));
        groupAvatar = ((ImageView) itemView.findViewById(R.id.event_viewholder_group_avatar));
        empyEventsField = ((TextView) itemView.findViewById(R.id.event_viewholder_empty_text));
    }

    private static final int GROUP_AVATAR_SIZE = 24;

    @Override
    public void bindView(int position, Event item, EventsClickListener actionListener) {
        if (item instanceof EmptyEvent) {
            emptyListIndicator.setVisibility(View.VISIBLE);
            eventText.setVisibility(View.GONE);
            eventTime.setVisibility(View.GONE);
            eventImage.setVisibility(View.GONE);
            docContainer.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
            groupAvatar.setVisibility(View.GONE);
            groupTitle.setVisibility(View.GONE);
            String emptyEventString = ((EmptyEvent) item).getEmptyEventString();
            spanEmptyText(emptyEventString, empyEventsField);
            return;
        }

        separator.setVisibility(View.VISIBLE);
        eventText.setVisibility(View.VISIBLE);
        eventTime.setVisibility(View.VISIBLE);
        eventImage.setVisibility(View.VISIBLE);
        docContainer.setVisibility(View.VISIBLE);
        emptyListIndicator.setVisibility(View.GONE);
        groupAvatar.setVisibility(View.VISIBLE);
        eventTime.setText(DateUtils.getHourMinuteString(item.getTime(), eventTime.getContext()));
        eventText.setText(item.getContent());
        groupTitle.setVisibility(View.VISIBLE);
        groupTitle.setText(item.getEventTitle());
        AvatarView.ImageViewTarget target = new AvatarView.ImageViewTarget(groupAvatar);
        Glide.with(groupAvatar.getContext())
                .load(item.getEventAvatarUrl())
                .asBitmap()
                .override(GROUP_AVATAR_SIZE, GROUP_AVATAR_SIZE)
                .error(R.drawable.ic_cave)
                .into(target);
        if (item.getImages() != null) {
            eventImage.setVisibility(View.VISIBLE);
            Event.Image image = item.getImages().get(0);
            Glide.with(eventImage.getContext())
                    .load(image.getUrl())
                    .thumbnail(0.3f)
                    .centerCrop()
                    .into(eventImage);
            eventImage.setOnClickListener(v -> actionListener.onPhotoClick(image.getUrl()));
        } else {
            eventImage.setVisibility(View.GONE);
        }
        List<Event.Document> documents = item.getDocuments();
        if (documents != null) {
            docContainer.setVisibility(View.VISIBLE);
            String fileUrl = documents.get(0).getUrl();
            String fileExtension = fileUrl.substring(fileUrl.lastIndexOf(StringUtils.DOT));
            String docButtonFormat = docContainer.getContext().getString(R.string.events_items_button_doc_format);
            docButton.setText(String.format(docButtonFormat, fileExtension));
            docButton.setOnClickListener(v -> {

                actionListener.onDocumentClick(fileUrl);
            });
        } else {
            docContainer.setVisibility(View.GONE);
        }
    }

    protected void spanEmptyText(String emptyEventString, TextView emptyEventField) {
        Spannable spannable = new SpannableString(emptyEventString);
        int endOfString = emptyEventString.length();
        spannable.setSpan(new StyleSpan(Typeface.BOLD), endOfString - 1, endOfString, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        emptyEventField.setText(spannable);
    }

    public interface EventsClickListener extends BindableViewHolder.ActionListener<Event> {
        void onPhotoClick(String photoUri);

        void onDocumentClick(String documentUri);
    }
}
