package com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangePasswordScope {

    @ChangePasswordScope
    @Subcomponent(modules = ChangePasswordModule.class)
    interface ChangePasswordComponent {
        void inject(ChangePasswordFragment changePasswordFragment);
    }

    @Module
    class ChangePasswordModule {
        private final ChangePasswordContract.View view;

        public ChangePasswordModule(ChangePasswordContract.View view) {
            this.view = view;
        }

        @Provides
        @ChangePasswordScope
        ChangePasswordContract.View provideView() {
            return this.view;
        }

    }
}
