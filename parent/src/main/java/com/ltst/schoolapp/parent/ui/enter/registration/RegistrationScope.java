package com.ltst.schoolapp.parent.ui.enter.registration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationScope {

    @RegistrationScope
    @Subcomponent(modules = Module.class)
    interface Component {

        void inject(RegistrationFragment registrationFragment);
    }

    @dagger.Module
    class Module {
        private final RegistrationContract.View view;

        public Module(RegistrationContract.View view) {
            this.view = view;
        }

        @Provides
        @RegistrationScope
        RegistrationContract.View provideView() {
            return this.view;
        }
    }

}
