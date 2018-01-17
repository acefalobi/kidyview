package com.ltst.schoolapp.teacher.ui.conversation.fragment;


import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ConversationContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void clearNotification(Conversation conversation);
    }

    interface View extends BaseView<Presenter> {
        void initView(LayerClient layerClient, String[] participantIds, String screenTitle);

        void initMessageComposer(LayerClient layerClient);
    }
}
