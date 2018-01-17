package com.ltst.schoolapp.parent.firebase.message.notification;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.data.model.Post;
import com.ltst.core.firebase.PushNotification;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.main.BottomScreen;
import com.ltst.schoolapp.parent.ui.main.MainActivity;
import com.ltst.schoolapp.parent.ui.report.ReportActivity;

import java.util.Map;

public class ReportNotification extends PushNotification {

    public static final String TYPE = "post";


    public ReportNotification(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override protected PendingIntent configurePendingIntent(Context context) {
        Intent result = new Intent(getChildId(), null, context, ReportActivity.class)
                .setPackage(context.getApplicationContext().getPackageName())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Post postFromMessage = getPostFromMessage();
        result.putExtra(ReportActivity.Screen.KEY_REPORT_POST, postFromMessage);
        result.putExtra(KEY_FROM_PUSH, true);
        return PendingIntent.getActivity(context, 1, result, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static final String CHILD_ID_KEY = "id";
    private static final String FIRST_NAME_KEY = "child_first_name";
    private static final String LAST_NAME_KEY = "child_last_name";
    private static final String CREATED_AT_KEY = "created_at";
    private static final String AVATAR_URL_KEY = "child_avatar_url";

    @Nullable
    private Post getPostFromMessage() {
        Map<String, String> data = remoteMessage.getData();
        long id = Integer.valueOf(data.get(CHILD_ID_KEY));
        String firstName = data.get(FIRST_NAME_KEY);
        String lastName = data.get(LAST_NAME_KEY);
        String createdAt = data.get(CREATED_AT_KEY);
        String childAvatarUrl = data.get(AVATAR_URL_KEY);
        return new Post(id, createdAt, firstName, lastName, childAvatarUrl);
    }

    private String getChildId() {
        Map<String, String> data = remoteMessage.getData();
        return data.get(CHILD_ID_KEY);
    }


    @Override protected int largeIcon() {
        return R.drawable.ic_launcher;
    }

    @Override protected String getNotificationTag() {
        return getClass().getName() + getChildId(); //unique tag for one
    }
}
