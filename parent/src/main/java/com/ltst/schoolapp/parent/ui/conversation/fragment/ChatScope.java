package com.ltst.schoolapp.parent.ui.conversation.fragment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatScope {
    @Module
    class ChatModule {
        private final ConversationContract.View view;

        public ChatModule(ConversationContract.View view) {
            this.view = view;
        }

        @Provides
        @ChatScope ConversationContract.View provideView() {
            return view;
        }
    }

    @ChatScope
    @Subcomponent(modules = ChatModule.class)
    interface ChatComponent {

        void inject(ConversationFragment conversationFragment);
    }
}
