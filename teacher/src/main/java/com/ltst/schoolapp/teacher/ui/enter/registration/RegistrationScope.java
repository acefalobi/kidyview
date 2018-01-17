package com.ltst.schoolapp.teacher.ui.enter.registration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationScope {

    @RegistrationScope
    @Subcomponent(modules = RegistrationModule.class)
    interface RegistrationComponent {

        void inject(RegistrationFragment registrationFragment);
    }

    @Module
    class RegistrationModule {
        private final RegistrationContract.View view;

        public RegistrationModule(RegistrationContract.View view) {
            this.view = view;
        }

        @Provides
        @RegistrationScope
        RegistrationContract.View provideRegistrationView() {
            return this.view;
        }
    }
}
