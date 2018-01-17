package com.ltst.schoolapp.teacher.ui.checks.select.child.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChecksSelectChildScope {

    @ChecksSelectChildScope
    @Subcomponent(modules = ChecksSelectChildModule.class)
    interface ChecksSelectChildComponent {
        void inject(ChecksSelectChildFragment checksSelectChildFragment);
    }

    @Module
    class ChecksSelectChildModule {
        private final ChecksSelectChildContract.View view;

        public ChecksSelectChildModule(ChecksSelectChildContract.View view) {
            this.view = view;
        }

        @Provides
        @ChecksSelectChildScope ChecksSelectChildContract.View provideEditProfileView() {
            return this.view;
        }


    }
}
