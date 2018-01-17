package com.ltst.core.util;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class ActivityProvider {
    public ActivityResultListener activityResultListener;

    public void setActivityResultListener(ActivityResultListener activityResultListener) {
        this.activityResultListener = activityResultListener;
    }

    private AppCompatActivity activity;

    public void attach(AppCompatActivity appCompatActivity) {
        this.activity = appCompatActivity;
    }

    public void detach(AppCompatActivity appCompatActivity) {
        this.activity = null;
    }

    public AppCompatActivity getContext() {
         return activity;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (activityResultListener != null) {
            activityResultListener.onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface ActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

}
