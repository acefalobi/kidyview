package com.livetyping.utils.utils.compat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Danil on 05.08.2016.
 */
class BaseCompatUtilsImpl implements CompatUtilsImpl {
    @Override
    public void setBackground(View view, Drawable background) {
        view.setBackgroundDrawable(background);
    }

    @Override
    public int getColor(Context context, int id) {
        return context.getResources().getColor(id);
    }
}
