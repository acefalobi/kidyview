package com.ltst.schoolapp.parent.ui.enter.newpass;

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

    @Module
    class NewPassModule {
        private final NewPassContract.View view;
        private final Bundle screenParams;

        public NewPassModule(NewPassContract.View view, Bundle screenParams) {
            this.view = view;
            this.screenParams = screenParams;
        }

        @Provides
        @NewPassScope NewPassContract.View provideView() {
            return this.view;
        }

        @Provides
        @NewPassScope Bundle provideScreenParams() {
            return this.screenParams;
        }
    }

    @NewPassScope
    @Subcomponent(modules = NewPassScope.NewPassModule.class)
    interface NewPassComponent {

        void inject(NewPassFragment newPassFragment);
    }
}
