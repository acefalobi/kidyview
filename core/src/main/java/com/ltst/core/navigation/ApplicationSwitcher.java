package com.ltst.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.ltst.core.util.ActivityConnector;
import com.ltst.core.util.IntentsUtil;

import java.io.File;

public class ApplicationSwitcher extends ActivityConnector<Activity> {
    public static final int CAMERA_REQUEST = 1678;
    public static final int GALLERY_REQUEST = 1786;
    public static final int FILE_REQUEST = 1763;


    public void openEmailApplication(String email) {
        openEmailApplication(email, "");
    }

    public void openEmailApplication(String email, String subject) {
        openEmailApplication(email, subject, "");
    }

    public void openEmailApplication(String email, String subject, String text) {
        final Activity activity = getAttachedObject();
        if (activity == null) return;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    public void openGooglePlayAppPage() {
        final Activity activity = getAttachedObject();
        if (activity == null) return;

        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public Uri openCamera(File file) {
        final Activity activity = getAttachedObject();
        if (activity == null) return null;
        Uri photoPath = Uri.fromFile(file);
        Intent intent = IntentsUtil.getCameraResultIntent(photoPath);
        activity.startActivityForResult(intent, CAMERA_REQUEST);
        return photoPath;
    }

    public void openGallery() {
        final Activity activity = getAttachedObject();
        if (activity == null) return;
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        activity.startActivityForResult(intent, GALLERY_REQUEST);
    }

    public void openDial(String phoneNumber) {
        final Activity activity = getAttachedObject();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }

    public boolean fileManagerExist() {
        final Activity activity = getAttachedObject();
        if (activity == null) return false;
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void openFileManager() {
        final Activity activity = getAttachedObject();
        if (activity == null) return;
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, ""), FILE_REQUEST);
    }
}
