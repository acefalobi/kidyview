package com.livetyping.utils.utils;

import android.view.View;

/**
 * Created by Danil on 21.04.2016.
 */
public class PaddingUtils {

    public static void setLeftPadding(View view, int padding) {
        view.setPadding(padding, view.getPaddingTop(), view.getPaddingRight(),
                view.getPaddingBottom());
    }

    public static void setRightPadding(View view, int padding) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), padding,
                view.getPaddingBottom());
    }

    public static void setTopPadding(View view, int padding) {
        view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(),
                view.getPaddingBottom());
    }

    public static void setBottomPadding(View view, int padding) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(),
                padding);
    }
}
