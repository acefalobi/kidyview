package com.ltst.schoolapp.parent.ui.main.chats;


import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ChatsContract {

    interface Presenter extends BasePresenter {
        void openSingleConversation(Conversation conversation);

        void openAddDialogMemberScreen();
    }

    interface View extends BaseView<Presenter> {
        void bindView(LayerClient layerClient);
        void startLoad();
        void stopLoad();
    }
}
