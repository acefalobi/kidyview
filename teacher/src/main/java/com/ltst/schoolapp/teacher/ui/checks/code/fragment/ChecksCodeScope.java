package com.ltst.schoolapp.teacher.ui.checks.code.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChecksCodeScope {

    @ChecksCodeScope
    @Subcomponent(modules = ChecksCodeModule.class)
    interface ChecksCodeComponent {
        void inject(ChecksCodeFragment checksCodeFragment);
    }

    @Module
    class ChecksCodeModule {
        private final ChecksCodeContract.View view;

        public ChecksCodeModule(ChecksCodeContract.View view) {
            this.view = view;
        }

        @Provides
        @ChecksCodeScope
        ChecksCodeContract.View provideEditProfileView() {
            return this.view;
        }
    }
}
