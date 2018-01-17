package com.ltst.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.util.ActivityConnector;

import java.security.InvalidParameterException;


/**
 * Class for swithcing activities in application
 */
public class ActivityScreenSwitcher extends ActivityConnector<CoreActivity> implements
        ScreenSwitcher<ActivityScreen> {

    /**
     * Used for switch next ActivityScreen
     *
     * @param screen - instance of ActivityScreen
     * @see ActivityScreen
     */
    @Override
    public void open(ActivityScreen screen) {
        final Activity activity = getAttachedObject();
        if (activity == null) {
            return;
        }
        if (screen != null) {
            Intent intent = screen.intent(activity);
            ActivityCompat.startActivity(activity, intent, screen.activityOptions(activity));
        } else {
            throw new InvalidParameterException("Screen doesn't null");
        }
    }

    /**
     * go back with activity stack
     */
    @Override
    public void goBack() {
        final Activity activity = getAttachedObject();
        if (activity != null) {
            activity.onBackPressed();
        }
    }

    /**
     * @param screen
     * @param requestCode - request code for startActivityDorResult();
     */
    public void startForResult(final ActivityScreen screen, final int requestCode) {
        final Activity activity = getAttachedObject();
        if (activity == null) {
            return;
        }
        if (screen != null) {
            ActivityScreen activityScreen = screen;
            Intent intent = activityScreen.intent(activity);
            ActivityCompat.startActivityForResult(
                    activity,
                    intent,
                    requestCode,
                    activityScreen.activityOptions(activity));
        } else {
            throw new InvalidParameterException("Screen doesn't null");
        }
    }

    /**
     * used Intent data for transfer data to previos activity
     *
     * @param data
     */
    public void setResultAndGoBack(@Nullable Intent data) {
        final Activity activity = getAttachedObject();
        if (activity == null)
            return;
        if (data != null)
            activity.setResult(Activity.RESULT_OK, data);
        goBack();
    }

    public void setResultAndFinish(@Nullable Intent data) {
        final Activity activity = getAttachedObject();
        if (activity == null)
            return;
        if (data != null)
            activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    public void overridePendingTransition(int enterAnim, int exitAnim) {
        final Activity activity = getAttachedObject();
        if (activity == null)
            return;
        activity.overridePendingTransition(enterAnim, exitAnim);
        goBack();
    }
}
