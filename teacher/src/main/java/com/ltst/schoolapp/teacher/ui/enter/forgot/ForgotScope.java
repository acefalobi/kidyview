package com.ltst.schoolapp.teacher.ui.enter.forgot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ForgotScope {

    @ForgotScope
    @Subcomponent (modules = ForgotModule.class)
    interface ForgotComponent {
        void inject(ForgotFragment fragment);
    }

    @Module
    class ForgotModule {
        private final ForgotContract.View view;

        public ForgotModule(ForgotContract.View view) {
            this.view = view;
        }

        @Provides
        @ForgotScope
        ForgotContract.View provideForgotView() {
            return this.view;
        }
    }
}
