package com.ltst.core.firebase;


import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.DrawableRes;

import com.google.firebase.messaging.RemoteMessage;
import com.livetyping.utils.utils.StringUtils;

import java.util.Map;

public abstract class PushNotification {
    public static final String KEY_FROM_PUSH = "PushNotificationCreator.FromNotification";

    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "body";

    protected RemoteMessage remoteMessage;
    private String title;
    private String content;

    public PushNotification(RemoteMessage remoteMessage) {
        this.remoteMessage = remoteMessage;
        setTitle(remoteMessage);
        setContent(remoteMessage);
    }

    protected void setTitle(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        for (String key : data.keySet()) {
            if (key.trim().equals(KEY_TITLE)) {
                title = data.get(key);
                return;
            }
        }
        title = StringUtils.EMPTY;
    }

    protected void setContent(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        for (String key : data.keySet()) {
            if (key.trim().equals(KEY_CONTENT)) {
                content = data.get(key);
                return;
            }
        }
        content = StringUtils.EMPTY;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    protected abstract PendingIntent configurePendingIntent(Context context);

    protected abstract @DrawableRes int largeIcon();

    protected abstract String getNotificationTag();

}
