package com.livetyping.utils.utils.compat;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Created by Danil on 05.08.2016.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class JCompatUtilsImpl extends BaseCompatUtilsImpl {
    @Override
    public void setBackground(View view, Drawable background) {
        view.setBackground(background);
    }
}
