package com.ltst.core.util;

import android.graphics.Bitmap;
import android.net.Uri;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Danil on 13.09.2016.
 */
public abstract class SimpleGlideListener implements RequestListener<Uri, Bitmap> {
    @Override
    public final boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean
            isFirstResource) {
        return false;
    }

    @Override
    public final boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean
            isFromMemoryCache, boolean isFirstResource) {
        onResourceReady();
        return false;
    }

    public abstract void onResourceReady();
}
