package com.ltst.schoolapp.teacher.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.ui.conversation.ConversationActivity;

import javax.inject.Inject;

public class LayerPushReceiver extends BroadcastReceiver {

    @Inject LayerNotificationsHelper layerNotificationsHelper;

    @Override public void onReceive(Context context, Intent intent) {
        TeacherApplication teacherApplication = (TeacherApplication) context.getApplicationContext();
        teacherApplication.getTeacherComponent()
                .receiverComponent(new ReceiverComponent.ReceiverModule())
                .inject(this);
        layerNotificationsHelper.setLargeIconResId(R.drawable.ic_launcher);
        layerNotificationsHelper.setSingleConversationActivity(ConversationActivity.class);
        layerNotificationsHelper.newAction(intent);
    }
}
