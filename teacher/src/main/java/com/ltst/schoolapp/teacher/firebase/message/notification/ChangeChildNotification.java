package com.ltst.schoolapp.teacher.firebase.message.notification;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.PushNotification;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.child.ChildActivity;

import java.util.Map;

public class ChangeChildNotification extends PushNotification {

    public static final String TYPE = "child";


    public ChangeChildNotification(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    public static final String INTENT_KEY_CHILD_ID = "ChangeChildNotififcation.ChildServerId";

    @Override protected PendingIntent configurePendingIntent(Context context) {
        Intent result = new Intent(String.valueOf(getChildServerId()) + TYPE, null, context, ChildActivity.class)
                .setPackage(context.getApplicationContext().getPackageName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(KEY_FROM_PUSH, true)
                .putExtra(INTENT_KEY_CHILD_ID, getChildServerId());
        return PendingIntent.getActivity(context, 1, result, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static final String KEY_CHILD_SERVER_ID = "id";

    private long getChildServerId() {
        Map<String, String> data = remoteMessage.getData();
        for (String key : data.keySet()) {
            if (key.trim().equals(KEY_CHILD_SERVER_ID)) {
                return Long.valueOf(data.get(key));
            }
        }
        return 0;
    }

    @Override protected int largeIcon() {
        return R.drawable.ic_launcher;
    }


    @Override protected String getNotificationTag() {
        return getClass().getName();
    }
}
