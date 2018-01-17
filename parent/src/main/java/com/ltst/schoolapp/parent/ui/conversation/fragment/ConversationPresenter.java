package com.ltst.schoolapp.parent.ui.conversation.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

import javax.inject.Inject;

public class ConversationPresenter implements ConversationContract.Presenter {

    private final ConversationContract.View view;
    private final LayerClient layerClient;
    private final String[] layerIdentitis;
    private final String screenTitle;
    private final ActivityScreenSwitcher activitySwitcher;
    private final LayerNotificationsHelper layerNotificationsHelper;
    private final boolean openedFromNotification;

    @Inject
    public ConversationPresenter(ConversationContract.View view,
                                 LayerClient layerClient,
                                 String[] layerIdentitis,
                                 String screenTitle, ActivityScreenSwitcher activitySwitcher,
                                 LayerNotificationsHelper layerNotificationsHelper,
                                 boolean openedFromNotification) {
        this.view = view;
        this.layerClient = layerClient;
        this.layerIdentitis = layerIdentitis;
        this.screenTitle = screenTitle;
        this.activitySwitcher = activitySwitcher;
        this.layerNotificationsHelper = layerNotificationsHelper;
        this.openedFromNotification = openedFromNotification;
    }

    @Override public void start() {
        view.initView(layerClient, layerIdentitis, screenTitle);
    }

    @Override public void stop() {

    }

    @Override public void firstStart() {
        view.initMessageComposer(layerClient);
    }

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }

    @Override public void goBack() {
        if (openedFromNotification) {
            activitySwitcher.open(new MainActivity.Screen());
        } else {
            activitySwitcher.goBack();
        }

    }

    @Override public void clearNotification(Conversation conversation) {
        layerNotificationsHelper.clearNotificationOfConversation(conversation.getId().toString());
    }
}
