package com.ltst.schoolapp.teacher.ui.checks.other.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChecksOtherScope {

    @ChecksOtherScope
    @Subcomponent(modules = ChecksOtherModule.class)
    interface ChecksOtherComponent {
        void inject(ChecksOtherFragment checksOtherFragment);
    }

    @Module
    class ChecksOtherModule {
        private final ChecksOtherContract.View view;

        public ChecksOtherModule(ChecksOtherContract.View view) {
            this.view = view;
        }

        @Provides
        @ChecksOtherScope
        ChecksOtherContract.View provideEditProfileView() {
            return this.view;
        }
    }
}
