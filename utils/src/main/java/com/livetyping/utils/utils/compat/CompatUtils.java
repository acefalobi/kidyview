package com.livetyping.utils.utils.compat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.view.View;

public class CompatUtils {
    static final CompatUtilsImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new MCompatUtils();
        } else if (version >= 16) {
            IMPL = new JCompatUtilsImpl();
        } else {
            IMPL = new BaseCompatUtilsImpl();
        }
    }

    public static void setBackground(View view, Drawable background) {
        IMPL.setBackground(view, background);
    }

    public static int getColor(Context context, @ColorRes int colorRes) {
        return IMPL.getColor(context, colorRes);
    }
}
