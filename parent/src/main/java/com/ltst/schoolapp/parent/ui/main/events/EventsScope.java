package com.ltst.schoolapp.parent.ui.main.events;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EventsScope {

    @EventsScope
    @Subcomponent(modules = EventsModule.class)
    interface EventsComponent {
        void inject (EventsFragment fragment);
    }

    @Module
    class EventsModule {
        private final EventsContract.View view;

        public EventsModule(EventsContract.View view) {
            this.view = view;
        }

        @Provides
        @EventsScope
        EventsContract.View provideView() {
            return this.view;
        }
    }
}
