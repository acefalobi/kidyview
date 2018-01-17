package com.ltst.schoolapp.parent.ui.main.chats;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatsScope {

    @Module
    class ChatsModule {
        private final ChatsContract.View view;

        public ChatsModule(ChatsContract.View view) {
            this.view = view;
        }

        @Provides
        @ChatsScope
        ChatsContract.View provideView() {
            return view;
        }
    }

    @ChatsScope
    @Subcomponent(modules = ChatsScope.ChatsModule.class)
    interface ChatsComponent {

        void inject(ChatsFragment chatsFragment);
    }

}
