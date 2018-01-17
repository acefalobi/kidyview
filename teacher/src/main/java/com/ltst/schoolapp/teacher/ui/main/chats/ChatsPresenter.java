package com.ltst.schoolapp.teacher.ui.main.chats;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.conversation.ConversationActivity;
import com.ltst.schoolapp.teacher.ui.main.ChangeGroupHelper;
import com.ltst.schoolapp.teacher.ui.select.dialog.SelectDialogMemberActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatsPresenter implements ChatsContract.Presenter {

    private final ChatsContract.View view;
    private final LayerClient layerClient;
    private final ActivityScreenSwitcher activitySwitcher;
    private final DataService dataService;
    private final ChangeGroupHelper spinnerHelper;

    private CompositeSubscription subscription;

    @Inject
    public ChatsPresenter(ChatsContract.View view,
                          LayerClient layerClient,
                          ActivityScreenSwitcher activitySwitcher,
                          DataService dataService,
                          ChangeGroupHelper spinnerHelper) {
        this.view = view;
        this.layerClient = layerClient;
        this.activitySwitcher = activitySwitcher;
        this.dataService = dataService;
        this.spinnerHelper = spinnerHelper;
    }

    @Override public void firstStart() {

    }


    @Override public void start() {
        spinnerHelper.showSpinner(false);
        subscription = new CompositeSubscription();
        view.startLoad();
        subscription.add(dataService.layerConnect()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(layerConnected,Throwable::printStackTrace));

//        view.bindView(layerClient);
    }

    private final Action1<Boolean> layerConnected = connect -> {
        if (connect) {
            ChatsPresenter.this.view.stopLoad();
            ChatsPresenter.this.view.bindView(ChatsPresenter.this.layerClient);
        }

    };

    @Override public void stop() {
        subscription.unsubscribe();
        subscription = null;
    }


    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }

    @Override public void openSingleConversation(Conversation conversation) {
        Set<Identity> participants = conversation.getParticipants();
        List<String> ids = new ArrayList<>(participants.size());
        for (Identity participant : participants) {
            ids.add(participant.getUserId());
        }
        String[] arrayIds = new String[ids.size()];
        arrayIds = ids.toArray(arrayIds);
        activitySwitcher.open(new ConversationActivity.Screen(arrayIds, StringUtils.EMPTY)); //title will be init with atlas utils
    }

    @Override public void openAddDialogMemberScreen() {
        SelectDialogMemberActivity.Screen screen = new SelectDialogMemberActivity
                .Screen(SelectDialogMemberActivity.START_SINGLE_CHAT);
        activitySwitcher.open(screen);
    }
}
