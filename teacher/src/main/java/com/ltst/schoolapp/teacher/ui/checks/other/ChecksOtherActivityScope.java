package com.ltst.schoolapp.teacher.ui.checks.other;

import android.os.Bundle;

import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.checks.other.fragment.ChecksOtherScope;
import com.ltst.schoolapp.teacher.ui.checks.other.fragment.ChecksOtherScope.ChecksOtherModule;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface ChecksOtherActivityScope {

    @ChecksOtherActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {ChecksOtherActivityModule.class})
    interface ChecksOtherActivityComponent {
        void inject(ChecksOtherActivity activity);

        ChecksOtherScope.ChecksOtherComponent checksOtherComponent(ChecksOtherModule module);

    }

    @Module
    public class ChecksOtherActivityModule {
        private Bundle activityParams;

        public ChecksOtherActivityModule(Bundle activityParams) {
            this.activityParams = activityParams;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }
    }
}
