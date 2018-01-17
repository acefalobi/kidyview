package com.ltst.schoolapp.parent.firebase.message;


import com.google.firebase.messaging.RemoteMessage;
import com.ltst.core.firebase.BaseFcmService;
import com.ltst.core.firebase.PushNotificationCreator;
import com.ltst.schoolapp.parent.ParentApplication;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class ParentFireBaseMessageService extends BaseFcmService {

    @Inject PushNotificationCreator notificationCreator;

    @Override public void onCreate() {
        super.onCreate();
        ParentApplication application = (ParentApplication) getApplicationContext();
        application.getComponent().fireBaseMessageComponent().inject(this);
    }

    @Override protected void handleFireBaseMessage(RemoteMessage remoteMessage) {
        notificationCreator.showNotification(getApplicationContext(), remoteMessage);
    }


}
