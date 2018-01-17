package com.ltst.schoolapp.parent.receviers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.ui.conversation.ConversationActivity;

import javax.inject.Inject;

public class LayerPushReceiver extends BroadcastReceiver {


    @Inject LayerNotificationsHelper layerNotificationsHelper;

    @Override public void onReceive(Context context, Intent intent) {
        ParentApplication application = ((ParentApplication) context.getApplicationContext());
        application.getComponent().receiverComponent().inject(this);
        layerNotificationsHelper.setLargeIconResId(R.drawable.ic_launcher);
        layerNotificationsHelper.setSingleConversationActivity(ConversationActivity.class);
        layerNotificationsHelper.newAction(intent);
    }


}
