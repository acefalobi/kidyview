package com.ltst.core.layer.atlas;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.layer.atlas.messagetypes.threepartimage.CameraSender;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.IntentsUtil;

import java.io.File;

public class FixedCameraSender extends CameraSender {

    public FixedCameraSender(int titleResId, Integer iconResId, Activity activity, @NonNull String fileProviderAuthority) {
        super(titleResId, iconResId, activity, fileProviderAuthority);
    }

    public FixedCameraSender(String title, Integer iconResId, Activity activity, @NonNull String fileProviderAuthority) {
        super(title, iconResId, activity, fileProviderAuthority);
    }

    @NonNull @Override protected File getTempFile(Activity activity) {
        return FilePathUtil.getCacheDir(activity);
    }

    @Override protected void openCamera(Activity activity, File file, int requestCode) {
        Uri photoPath = Uri.fromFile(file);
        Intent intent = IntentsUtil.getCameraResultIntent(photoPath);
        activity.startActivityForResult(intent, requestCode);
    }
}
