package com.livetyping.utils.utils.compat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Danil on 05.08.2016.
 */
interface CompatUtilsImpl {
    void setBackground(View view, Drawable background);

    int getColor(Context context, int id);
}
