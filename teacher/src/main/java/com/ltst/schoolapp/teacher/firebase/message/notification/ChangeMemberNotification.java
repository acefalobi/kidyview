package com.ltst.schoolapp.teacher.firebase.message.notification;


import android.app.PendingIntent;
import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.PushNotification;
import com.ltst.schoolapp.R;

public class ChangeMemberNotification extends PushNotification {

    public static final String TYPE = "family_member";

    public ChangeMemberNotification(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override protected PendingIntent configurePendingIntent(Context context) {
        return null;
    }

    @Override protected int largeIcon() {
        return R.drawable.ic_launcher;
    }

    @Override protected String getNotificationTag() {
        return getClass().getName();
    }
}
