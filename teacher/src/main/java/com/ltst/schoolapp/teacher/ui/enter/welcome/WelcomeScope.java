package com.ltst.schoolapp.teacher.ui.enter.welcome;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface WelcomeScope {

    @Subcomponent(modules = WelcomeModule.class)
    interface WelcomeComponent {

        void inject(WelcomeFragment welcomeFragment);
    }

    @Module
    class WelcomeModule {
        private final WelcomeContract.View view;

        public WelcomeModule(WelcomeContract.View view) {
            this.view = view;
        }
        
        @Provides
        @WelcomeScope
        WelcomeContract.View provideWelcomeView(){
            return view;
        }
    }
}
