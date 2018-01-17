package com.ltst.schoolapp.teacher.ui.checks.select.child;

import android.os.Bundle;

import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.checks.select.child.fragment.ChecksSelectChildScope.ChecksSelectChildComponent;
import com.ltst.schoolapp.teacher.ui.checks.select.child.fragment.ChecksSelectChildScope.ChecksSelectChildModule;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface ChecksSelectChildActivityScope {

    @ChecksSelectChildActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {ChecksSelectChildActivityModule.class})
    interface ChecksSelectChildActivityComponent {
        void inject(ChecksSelectChildActivity activity);

        ChecksSelectChildComponent checksSelectChildComponent(ChecksSelectChildModule module);

    }

    @Module
    public class ChecksSelectChildActivityModule {
        private final Bundle activityParams;
        private final DialogProvider dialogProvider;

        public ChecksSelectChildActivityModule(Bundle activityParams, DialogProvider dialogProvider) {
            this.activityParams = activityParams;
            this.dialogProvider = dialogProvider;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }

        @Provides
        @ChecksSelectChildActivityScope
        DialogProvider provideDialogProvider(){
            return dialogProvider;
        }
    }
}
