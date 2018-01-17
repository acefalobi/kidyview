package com.ltst.schoolapp.parent.firebase.message.notification;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.schoolapp.R;
import com.ltst.core.firebase.PushNotification;
import com.ltst.schoolapp.parent.ui.main.BottomScreen;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

public class CheckInNotification extends PushNotification {

    public static final String TYPE = "check_in";


    public CheckInNotification(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }


    @Override public PendingIntent configurePendingIntent(Context context) {
        Intent result = new Intent(context, MainActivity.class)
                .setPackage(context.getApplicationContext().getPackageName())
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(MainActivity.Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN, BottomScreen.CHECKS.toString());
        return PendingIntent.getActivity(context, 1, result, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override public int largeIcon() {
        return R.drawable.ic_launcher;
    }

    static final String NOTIFICATION_TAG = "CheckInNotification";

    @Override protected String getNotificationTag() {
        return NOTIFICATION_TAG;
    }
}
