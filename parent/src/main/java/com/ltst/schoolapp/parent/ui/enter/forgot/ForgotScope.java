package com.ltst.schoolapp.parent.ui.enter.forgot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ForgotScope {

    @Module
    class ForgotModule {
        private final ForgotContract.View view;

        public ForgotModule(ForgotContract.View view) {
            this.view = view;
        }

        @Provides
        @ForgotScope
        ForgotContract.View provideView (){
            return this.view;
        }
    }

    @ForgotScope
    @Subcomponent(modules = ForgotScope.ForgotModule.class)
    interface ForgotComponent {

        void inject(ForgotFragment forgotFragment);
    }
}
