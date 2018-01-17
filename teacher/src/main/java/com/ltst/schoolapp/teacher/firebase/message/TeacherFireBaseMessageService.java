package com.ltst.schoolapp.teacher.firebase.message;


import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.BaseFcmService;
import com.ltst.core.firebase.PushNotificationCreator;
import com.ltst.schoolapp.TeacherApplication;

import javax.inject.Inject;

public class TeacherFireBaseMessageService extends BaseFcmService {

    @Inject PushNotificationCreator notificationCreator;

    @Override public void onCreate() {
        super.onCreate();
        TeacherApplication teacherApplication = (TeacherApplication) getApplicationContext();
        teacherApplication.getTeacherComponent().fireBaseMessageComponent().inject(this);

    }

    @Override protected void handleFireBaseMessage(RemoteMessage remoteMessage) {
        notificationCreator.showNotification(getApplicationContext(), remoteMessage);
    }
}
