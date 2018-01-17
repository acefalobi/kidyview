package com.ltst.core.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;

public class AvatarView extends LinearLayout {

    private ImageView avatarImage;
    private ViewGroup underAvatarContainer;
    private ImageViewTarget imageViewTarget;
    private TextView underAvatarTextView;
    private ClickAvatarCallBack clickAvatarCallBack;


    public AvatarView(Context context) {
        super(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarView, 0, 0);
        boolean showUnderAvatar = a.getBoolean(R.styleable.AvatarView_show_under_avatar, true);
        Drawable background = a.getDrawable(R.styleable.AvatarView_av_background);
        a.recycle();
        LayoutInflater.from(getContext()).inflate(R.layout.avatar_view, this);
        avatarImage = ((ImageView) findViewById(R.id.avatar_view_camera));
        if (background != null) {
            avatarImage.setBackground(background);
        }
        underAvatarContainer = ((ViewGroup) findViewById(R.id.avatar_empty_underphoto_container));
        underAvatarContainer.setVisibility(showUnderAvatar ? VISIBLE : GONE);
        underAvatarTextView = ((TextView) findViewById(R.id.avatar_view_underphoto_text));
        ViewClickListener avatarViewClickListener = new ViewClickListener();
        avatarImage.setOnClickListener(avatarViewClickListener);
        imageViewTarget = new ImageViewTarget(avatarImage);
    }

    public void showUnderAvatar(boolean show) {
        underAvatarContainer.setVisibility(show ? VISIBLE : GONE);
    }

    public void setClickAvatarCallBack(ClickAvatarCallBack clickAvatarCallBack) {
        this.clickAvatarCallBack = clickAvatarCallBack;
    }

    public void emptyAvatarError() {
        TextView underPhotoText = (TextView) findViewById(R.id.avatar_view_underphoto_text);
        int errorColor = ContextCompat.getColor(getContext(), R.color.error_color);
        underPhotoText.setTextColor(errorColor);
        Drawable arrow = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_blue_up);
        arrow.mutate().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
        ((ImageView) findViewById(R.id.avatar_arrow)).setImageDrawable(arrow);

    }

    public void setAvatar(String photoUri) {
        if (StringUtils.isBlank(photoUri)) return;
        setAvatar(Uri.parse(photoUri));
    }

    private static final String STORAGE = "storage";
    private static final String FILE = "file://";

    public void setAvatar(Uri photoUri) {
//        int avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_view_avatar_size);
        int avatarSize = avatarImage.getBackground().getIntrinsicHeight();
        if (photoUri.toString().contains(STORAGE)) {
            photoUri = Uri.parse(FILE + photoUri.toString());
        }
        Glide.with(getContext()).load(photoUri)
                .asBitmap()
                .override(avatarSize, avatarSize)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageViewTarget);
        underAvatarContainer.setVisibility(GONE);

    }

    public void clearAvatar() {
        avatarImage.setImageDrawable(null);
    }

    public static class ImageViewTarget extends BitmapImageViewTarget {

        private ImageView imageView;

        public ImageViewTarget(ImageView view) {
            super(view);
            this.imageView = view;
        }

        @Override
        protected void setResource(Bitmap resource) {
            Resources resources = imageView.getResources();
            RoundedBitmapDrawable bitmap = RoundedBitmapDrawableFactory.create(resources, resource);
            bitmap.setCircular(true);
            imageView.setImageDrawable(bitmap);
        }
    }

    private class ViewClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.avatar_view_camera) {
                if (clickAvatarCallBack != null) {
                    clickAvatarCallBack.onAvatarClick();
                }
            }
        }
    }

    public interface ClickAvatarCallBack {
        void onAvatarClick();
    }
}
