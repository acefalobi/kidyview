package com.livetyping.utils.utils.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * Created by Danil on 05.08.2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class MCompatUtils extends JCompatUtilsImpl {
    @Override
    public int getColor(Context context, int id) {
        return context.getColor(id);
    }
}
