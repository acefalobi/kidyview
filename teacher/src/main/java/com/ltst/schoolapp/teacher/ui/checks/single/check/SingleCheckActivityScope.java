package com.ltst.schoolapp.teacher.ui.checks.single.check;

import android.os.Bundle;

import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.checks.single.check.fragment.SingleCheckScope.SingleCheckComponent;
import com.ltst.schoolapp.teacher.ui.checks.single.check.fragment.SingleCheckScope.SingleCheckModule;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface SingleCheckActivityScope {

    @SingleCheckActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {SingleCheckActivityModule.class})
    interface SingleCheckActivityComponent {
        void inject(SingleCheckActivity activity);

        SingleCheckComponent singleCheckComponent(SingleCheckModule module);

    }

    @Module
    public class SingleCheckActivityModule {
        private Bundle activityParams;

        public SingleCheckActivityModule(Bundle activityParams) {
            this.activityParams = activityParams;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }
    }
}
