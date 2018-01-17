package com.ltst.schoolapp.parent.firebase.message;


import android.support.annotation.Nullable;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.PushNotification;
import com.ltst.core.firebase.PushNotificationCreator;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.firebase.message.notification.CheckInNotification;
import com.ltst.schoolapp.parent.firebase.message.notification.CheckOutPushNotification;
import com.ltst.schoolapp.parent.firebase.message.notification.EventNotification;
import com.ltst.schoolapp.parent.firebase.message.notification.ReportNotification;

public class ParentPushNotificationCreator extends PushNotificationCreator {


    public ParentPushNotificationCreator(ParentApplication context) {
        super(context);
    }

    @Override protected PushNotification factoryMethod(String messageType, RemoteMessage remoteMessage) {
        return getNotificationByType(messageType, remoteMessage);
    }


    @Nullable
    private PushNotification getNotificationByType(String type, RemoteMessage remoteMessage) {
        switch (type) {
            case CheckInNotification.TYPE:
                return new CheckInNotification(remoteMessage);
            case CheckOutPushNotification.TYPE:
                return new CheckOutPushNotification(remoteMessage);
            case EventNotification.TYPE:
                return new EventNotification(remoteMessage);
            case ReportNotification.TYPE:
                return new ReportNotification(remoteMessage);
            default:
                return null;
        }
    }

}
