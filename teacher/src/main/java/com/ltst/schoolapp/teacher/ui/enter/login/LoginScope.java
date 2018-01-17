package com.ltst.schoolapp.teacher.ui.enter.login;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginScope {

    @LoginScope
    @Subcomponent(modules = LoginModule.class)
    interface LoginComponent {

        void inject(LoginFragment loginFragment);
    }

    @Module
    class LoginModule {
        private final LoginContract.View view;

        public LoginModule(LoginContract.View view) {
            this.view = view;
        }

        @Provides
        @LoginScope
        LoginContract.View provideLoginView() {
            return this.view;
        }
    }
}
