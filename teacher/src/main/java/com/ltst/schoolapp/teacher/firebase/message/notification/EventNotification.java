package com.ltst.schoolapp.teacher.firebase.message.notification;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.PushNotification;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.events.calendar.CalendarActivity;

public class EventNotification extends PushNotification {

    public static final String TYPE = "event";

    public EventNotification(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override protected PendingIntent configurePendingIntent(Context context) {
        Intent result = new Intent(context, CalendarActivity.class)
                .setPackage(context.getApplicationContext().getPackageName())
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(KEY_FROM_PUSH, true);
        return PendingIntent.getActivity(context, 1, result, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override protected int largeIcon() {
        return R.drawable.ic_launcher;
    }

    @Override protected String getNotificationTag() {
        return getClass().getName();
    }
}
