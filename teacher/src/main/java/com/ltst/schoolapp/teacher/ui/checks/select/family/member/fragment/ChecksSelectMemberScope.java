package com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChecksSelectMemberScope {

    @ChecksSelectMemberScope
    @Subcomponent(modules = ChecksSelectMemberModule.class)
    interface ChecksSelectMemberComponent {
        void inject(ChecksSelectMemberFragment checksSelectMemberFragment);
    }

    @Module
    class ChecksSelectMemberModule {
        private final ChecksSelectMemberContract.View view;

        public ChecksSelectMemberModule(ChecksSelectMemberContract.View view) {
            this.view = view;
        }

        @Provides
        @ChecksSelectMemberScope
        ChecksSelectMemberContract.View provideEditProfileView() {
            return this.view;
        }
    }
}
