package com.ltst.schoolapp.teacher.ui.conversation;


import android.os.Bundle;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.layer.LayerModule;
import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.conversation.fragment.ChatScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface ConversationScope {

    @Module
    class ConversationModule {
        private final Bundle screenParams;

        public ConversationModule(Bundle screenParams) {
            this.screenParams = screenParams;
        }

        @Provides
        @ConversationScope
        String[] provideLayerIds() {
            return screenParams.getStringArray(LayerModule.LAYER_IDENTITIES_KEY);
        }

        @Provides
        @ConversationScope
        String provideScreenTitle() {
            if (screenParams.containsKey(LayerModule.LAYER_SCREEN_TITLE_KEY)) {
                return screenParams.getString(LayerModule.LAYER_SCREEN_TITLE_KEY);
            } else return StringUtils.EMPTY;
        }
        @Provides
        @ConversationScope
        boolean provideFromNotification() {
            if (screenParams.containsKey(LayerNotificationsHelper.FROM_NOTIFICATION_KEY)) {
                return screenParams.getBoolean(LayerNotificationsHelper.FROM_NOTIFICATION_KEY);
            } else return false;
        }
    }


    @ConversationScope
    @Component(dependencies = TeacherComponent.class, modules = ConversationModule.class)
    interface ConversationComponent {

        void inject(ConversationActivity conversationActivity);

        ChatScope.ChatComponent chatComponent(ChatScope.ChatModule module);

    }
}
