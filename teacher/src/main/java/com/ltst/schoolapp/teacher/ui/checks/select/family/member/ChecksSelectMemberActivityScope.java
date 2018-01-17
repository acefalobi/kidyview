package com.ltst.schoolapp.teacher.ui.checks.select.family.member;

import android.os.Bundle;

import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment.ChecksSelectMemberScope.ChecksSelectMemberComponent;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment.ChecksSelectMemberScope.ChecksSelectMemberModule;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface ChecksSelectMemberActivityScope {

    @ChecksSelectMemberActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {ChecksSelectMemberActivityModule.class})
    interface ChecksSelectMemberActivityComponent {
        void inject(ChecksSelectMemberActivity activity);

        ChecksSelectMemberComponent checksSelectMemberComponent(ChecksSelectMemberModule module);

    }

    @Module
    public class ChecksSelectMemberActivityModule {
        private Bundle activityParams;

        public ChecksSelectMemberActivityModule(Bundle activityParams) {
            this.activityParams = activityParams;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }
    }
}
