package com.ltst.schoolapp.teacher.ui.checks.check.the.code.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckTheCodeScope {

    @CheckTheCodeScope
    @Subcomponent(modules = CheckTheCodeModule.class)
    interface CheckTheCodeComponent {
        void inject(CheckTheCodeFragment checkTheCodeFragment);
    }

    @Module
    class CheckTheCodeModule {
        private final CheckTheCodeContract.View view;

        public CheckTheCodeModule(CheckTheCodeContract.View view) {
            this.view = view;
        }


        @Provides
        @CheckTheCodeScope
        CheckTheCodeContract.View provideEditProfileView() {
            return this.view;
        }
    }
}
