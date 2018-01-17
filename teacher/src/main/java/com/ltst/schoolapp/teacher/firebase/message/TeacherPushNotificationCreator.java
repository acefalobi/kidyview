package com.ltst.schoolapp.teacher.firebase.message;


import android.app.Application;
import android.support.annotation.Nullable;

import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.PushNotification;
import com.ltst.core.firebase.PushNotificationCreator;
import com.ltst.schoolapp.teacher.firebase.message.notification.ChangeChildNotification;
import com.ltst.schoolapp.teacher.firebase.message.notification.EventNotification;
import com.ltst.schoolapp.teacher.firebase.message.notification.FamilyRequestNotification;

public class TeacherPushNotificationCreator extends PushNotificationCreator {

    public TeacherPushNotificationCreator(Application context) {
        super(context);
    }

    @Nullable
    @Override
    protected PushNotification factoryMethod(String messageType, RemoteMessage remoteMessage) {
        if (messageType.equals(FamilyRequestNotification.TYPE)
                && remoteMessage.getData().containsKey(FamilyRequestNotification.KEY_CHILD_SERVER_ID)){
            return new FamilyRequestNotification(remoteMessage);
        }
        switch (messageType) {
            case EventNotification.TYPE:
                return new EventNotification(remoteMessage);
//            case ChangeMemberNotification.TYPE:
//                return new ChangeMemberNotification(remoteMessage);
            case ChangeChildNotification.TYPE:
                return new ChangeChildNotification(remoteMessage);
//            case FamilyRequestNotification.TYPE:
//                return new FamilyRequestNotification(remoteMessage);
            default:
                return null;
        }
    }
}
