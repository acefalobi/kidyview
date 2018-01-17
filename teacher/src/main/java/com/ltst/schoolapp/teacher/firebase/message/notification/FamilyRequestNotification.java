package com.ltst.schoolapp.teacher.firebase.message.notification;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.PushNotification;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.child.ChildActivity;

import java.util.Map;

public class FamilyRequestNotification extends PushNotification {

    public static final String TYPE = "family_member";

    public FamilyRequestNotification(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    public static final String INTENT_KEY_CHILD_ID = "FamilyRequestNotification.ChildServerId";
    public static final String INTENT_KEY_MEMBER_ID = "FamilyRequestNotification.MemberServerId";


    @Override protected PendingIntent configurePendingIntent(Context context) {
        Intent result = new Intent(String.valueOf(getChildServerId() + TYPE), null, context, ChildActivity.class)
                .setPackage(context.getApplicationContext().getPackageName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(KEY_FROM_PUSH, true)
                .putExtra(INTENT_KEY_CHILD_ID, getChildServerId())
                .putExtra(INTENT_KEY_MEMBER_ID, getMemberId());
        return PendingIntent.getActivity(context, 1, result, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static final String KEY_CHILD_SERVER_ID = "child_id";

    private long getChildServerId() {
        Map<String, String> data = remoteMessage.getData();
        for (String key : data.keySet()) {
            if (key.trim().equals(KEY_CHILD_SERVER_ID)) {
                return Long.valueOf(data.get(key));
            }
        }
        return 0;
    }

    private static final String KEY_MEMBER_ID = "id";

    private long getMemberId() {
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey(KEY_MEMBER_ID)) {
            return Long.valueOf(data.get(KEY_MEMBER_ID));
        } else return 0;
    }

    @Override protected int largeIcon() {
        return R.drawable.ic_launcher;
    }

    @Override protected String getNotificationTag() {
        return getClass().getName() + String.valueOf(getChildServerId());
    }
}
