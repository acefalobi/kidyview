package com.ltst.schoolapp.teacher.ui.checks.check.the.code;

import android.os.Bundle;

import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.checks.check.the.code.fragment.CheckTheCodeScope;
import com.ltst.schoolapp.teacher.ui.checks.check.the.code.fragment.CheckTheCodeScope.CheckTheCodeModule;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface CheckTheCodeActivityScope {

    @CheckTheCodeActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {CheckTheCodeActivityModule.class})
    interface CheckTheCodeActivityComponent {
        void inject(CheckTheCodeActivity activity);

        CheckTheCodeScope.CheckTheCodeComponent checkTheCodeComponent(CheckTheCodeModule module);

    }

    @Module
    public class CheckTheCodeActivityModule {
        private Bundle activityParams;

        public CheckTheCodeActivityModule(Bundle activityParams) {
            this.activityParams = activityParams;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }
    }
}
