package com.ltst.core.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;

/**
 * Created by Danil on 12.09.2016.
 */
public class TwoLineTextViewWithIcon extends FrameLayout {

    private ImageView imageView;
    private TextView mainTextView;
    private TextView secondaryTextView;
    private ImageView rightArrowView;

    public TwoLineTextViewWithIcon(Context context) {
        super(context);
    }

    public TwoLineTextViewWithIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwoLineTextViewWithIcon, 0, 0);
        Drawable leftDrawable = a.getDrawable(R.styleable.TwoLineTextViewWithIcon_left_icon);
        String mainText = a.getString(R.styleable.TwoLineTextViewWithIcon_main_text);
        String secondaryText = a.getString(R.styleable.TwoLineTextViewWithIcon_secondary_text);
        boolean showRightIcon = a.getBoolean(R.styleable.TwoLineTextViewWithIcon_right_arrow, false);
        a.recycle();
        LayoutInflater.from(getContext()).inflate(R.layout.two_line_text_view_with_left_icon, this);
        imageView = (ImageView) findViewById(R.id.two_line_text_view_with_left_icon_icon);
        mainTextView = (TextView) findViewById(R.id.two_line_text_view_with_left_icon_main_text);
        secondaryTextView = (TextView) findViewById(R.id.two_line_text_view_with_left_icon_secondary_text);
        rightArrowView = ((ImageView) findViewById(R.id.two_line_text_view_right_icon));
        imageView.setImageDrawable(leftDrawable);
        mainTextView.setText(mainText);
        if (StringUtils.isBlank(secondaryText)) {
            secondaryTextView.setVisibility(GONE);
        } else {
            secondaryTextView.setText(secondaryText);
        }
        if (showRightIcon) {
            ((View) rightArrowView.getParent()).setVisibility(VISIBLE);
        } else {
            ((View) rightArrowView.getParent()).setVisibility(GONE);
        }

    }

    public void setMainText(CharSequence text) {
        mainTextView.setText(text);
    }

    public void setSecondaryText(CharSequence text) {
        secondaryTextView.setText(text);
        secondaryTextView.setVisibility(VISIBLE);
    }
}
