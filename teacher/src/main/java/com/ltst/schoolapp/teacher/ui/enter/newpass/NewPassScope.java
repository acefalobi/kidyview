package com.ltst.schoolapp.teacher.ui.enter.newpass;

import android.os.Bundle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface NewPassScope {

    @NewPassScope
    @Subcomponent(modules = NewPassModule.class)
    interface NewPassComponent{
        void inject (NewPassFragment fragment);
    }

    @Module
    class NewPassModule {
        private final NewPassContract.View view;
        private final Bundle screenParams;

        public NewPassModule(NewPassContract.View view, Bundle screenArguments) {
            this.view = view;
            this.screenParams = screenArguments;
        }

        @Provides
        @NewPassScope
        NewPassContract.View provideNewPassView(){
            return this.view;
        }

        @Provides
        @NewPassScope
        Bundle provideScreenParams(){
            return this.screenParams;
        }
    }
}
