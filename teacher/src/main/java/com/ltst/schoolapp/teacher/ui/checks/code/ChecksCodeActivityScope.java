package com.ltst.schoolapp.teacher.ui.checks.code;

import android.os.Bundle;

import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.checks.code.fragment.ChecksCodeScope.ChecksCodeComponent;
import com.ltst.schoolapp.teacher.ui.checks.code.fragment.ChecksCodeScope.ChecksCodeModule;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface ChecksCodeActivityScope {

    @ChecksCodeActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {ChecksCodeActivityModule.class})
    interface ChecksCodeActivityComponent {
        void inject(ChecksCodeActivity activity);

        ChecksCodeComponent checksCodeComponent(ChecksCodeModule module);

    }

    @Module
    public class ChecksCodeActivityModule {
        private Bundle activityParams;

        public ChecksCodeActivityModule(Bundle activityParams) {
            this.activityParams = activityParams;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }
    }
}
