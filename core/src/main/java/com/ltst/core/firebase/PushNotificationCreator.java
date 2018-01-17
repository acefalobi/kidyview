package com.ltst.core.firebase;


import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.livetyping.utils.utils.DimenUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;

import java.util.Map;
import java.util.Set;

public abstract class PushNotificationCreator {

    private static final String KEY_NOTIFICATION_TAG = "PushNotificationCreator.TagKey";
    private static final String DEFAULT_TAG = "PushNotificationCreator.DefaultTag";

    private static final String KEY_TYPE = "type";

    private NotificationManager notificationManager;

    private static final @IdRes int SMALL_ICON_RES_ID = R.drawable.ic_notification_small;

    public PushNotificationCreator(Application context) {
        notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    public void showNotification(Context context, RemoteMessage remoteMessage) {
        String notificationType = getNotificationType(remoteMessage);
        PushNotification pushNotification = factoryMethod(notificationType, remoteMessage);
        if (pushNotification != null) {
            NotificationCompat.Builder builder = builderFromPushNotification(context, pushNotification);
            notify(builder);
        }
    }

    @Nullable
    protected abstract PushNotification factoryMethod(String messageType, RemoteMessage remoteMessage);

    private String getNotificationTag(NotificationCompat.Builder builder) {
        Bundle extras = builder.getExtras();
        if (extras.containsKey(KEY_NOTIFICATION_TAG)) {
            return extras.getString(KEY_NOTIFICATION_TAG);
        } else {
            return DEFAULT_TAG;
        }
    }

    private final static int DEFAULT_NOTIFICATION_ID = 15;

    private void notify(@NonNull NotificationCompat.Builder builder) {
        final String notificationTag = getNotificationTag(builder);
        notificationManager.cancel(notificationTag, DEFAULT_NOTIFICATION_ID);
        notificationManager.notify(notificationTag, DEFAULT_NOTIFICATION_ID, builder.build());
    }

    private String getNotificationType(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Set<String> dataKeySet = data.keySet();
        for (String key : dataKeySet) {
            if (key.trim().equals(KEY_TYPE)) {
                return data.get(key);
            }
        }
        return StringUtils.EMPTY;
    }

    private static final int LARGE_ICON_HEIGHT_IN_DP = 24;
    private static final int LARGE_ICON_WIDTH_IN_DP = 24;

    protected NotificationCompat.Builder builderFromPushNotification(Context context, PushNotification pushNotification) {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), pushNotification.largeIcon());
        int lagreIconHeight = DimenUtils.pxFromDp(context, LARGE_ICON_HEIGHT_IN_DP);
        int lagreIconWidth = DimenUtils.pxFromDp(context, LARGE_ICON_WIDTH_IN_DP);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(largeIcon, lagreIconWidth, lagreIconHeight, true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(SMALL_ICON_RES_ID)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle(pushNotification.getTitle())
                .setContentText(pushNotification.getContent())
                .setLargeIcon(scaledBitmap);
        builder.getExtras().putString(KEY_NOTIFICATION_TAG, pushNotification.getNotificationTag());
        builder.setContentIntent(pushNotification.configurePendingIntent(context));
        return builder;
    }

}

