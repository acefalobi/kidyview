package com.ltst.schoolapp.teacher.ui.main.feed;

import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ltst.core.data.uimodel.FeedType;
import com.ltst.core.util.EnumAdapter;
import com.ltst.schoolapp.R;

/**
 * Created by Danil on 22.09.2016.
 */

class FeedSpinnerAdapter extends EnumAdapter<FeedType> {

    static FeedSpinnerAdapter getStyled(Context context) {
        context = new ContextThemeWrapper(context, com.ltst.core.R.style
                .SpinnerTheme);
        return new FeedSpinnerAdapter(context, FeedType.class);
    }

    private FeedSpinnerAdapter(Context context, Class<FeedType> enumType) {
        super(context, FeedType.class);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.activity_main_spinner_item, null);
    }
}
