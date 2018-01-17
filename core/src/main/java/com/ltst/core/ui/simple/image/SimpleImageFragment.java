package com.ltst.core.ui.simple.image;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.navigation.FragmentScreen;

/**
 * Created by Danil on 22.09.2016.
 */
public class SimpleImageFragment extends DialogFragment {

    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 5032;
    private ImageView downloadButton;
    private ImageView image;
    private String url;
    private Bitmap bitmapForSave;
    private boolean canDownloadImage = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_simple_image, null, false);
        image = (ImageView) view.findViewById(R.id.fragment_simple_image);
        downloadButton = ((ImageView) view.findViewById(R.id.fragment_simple_download));
        url = getArguments().getString(Screen.URL_KEY);
        canDownloadImage = getArguments().getBoolean(Screen.CAN_DOWNLOAD_KEY);
        view.setOnClickListener(v -> dismiss());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        DrawableTypeRequest<String> load = Glide.with(getContext())
                .load(url);
        if (canDownloadImage) {
            SimpleTarget target = getTarget();
            load.into(target);
        } else {
            load.into(image);
        }

        return builder.create();
    }

    private SimpleTarget<GlideBitmapDrawable> getTarget() {
        return new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable resource, GlideAnimation<? super GlideBitmapDrawable> glideAnimation) {
                Bitmap bitmap = resource.getBitmap();
                bitmapForSave = bitmap;
                image.setImageBitmap(bitmap);
                if (canDownloadImage) {
                    downloadButton.setVisibility(View.VISIBLE);
                    downloadButton.setOnClickListener(v -> checkPermission());
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addPhotoToGallery();
            } else {
                downloadButton.setVisibility(View.GONE);
            }
        }
    }

    private void addPhotoToGallery() {
        Glide.with(getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                String insertImage = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                        bitmapForSave, "new image", url);
                if (!StringUtils.isBlank(insertImage)) {
                    Toast.makeText(getContext(), getString(R.string.photo_saved_to_gallery), Toast.LENGTH_SHORT).show();
                }
                dismiss();

            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showShowPermissionRationale();
            } else {
                requestPermission();
            }
        } else {
            addPhotoToGallery();
        }

    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void showShowPermissionRationale() {
        requestPermission();
    }

    public static final class Screen extends FragmentScreen {

        public static final String URL_KEY = "SimpleImageFragment.Screen.url";
        public static final String CAN_DOWNLOAD_KEY = "SimpleImageFragment.Screen.canDownload";
        private String url;
        private boolean canDownload = false;

        public Screen(String url) {
            this.url = url;
        }

        public Screen(String url, boolean canDownload) {
            this.url = url;
            this.canDownload = canDownload;
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
            arguments.putString(URL_KEY, url);
            arguments.putBoolean(CAN_DOWNLOAD_KEY, canDownload);
        }

        @Override
        protected Fragment createFragment() {
            return new SimpleImageFragment();
        }

    }
}
