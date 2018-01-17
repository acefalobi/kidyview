package com.ltst.schoolapp.teacher.ui.checks.dated;

import android.os.Bundle;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.main.ChangeGroupHelper;
import com.ltst.schoolapp.teacher.ui.main.checks.ChecksScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface DatedChecksScope {

    @Module
    class DatedChecksModule {

        private final Bundle screenParams;
        private final FragmentScreenSwitcher fragmentScreenSwitcher;

        public DatedChecksModule(Bundle screenParams, FragmentScreenSwitcher fragmentScreenSwitcher) {
            this.screenParams = screenParams;
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        }

        @Provides
        @DatedChecksScope Bundle provideScreenParams() {
            return this.screenParams;
        }

        @Provides
        @DatedChecksScope FragmentScreenSwitcher provideFragmentSwitcher() {
            return this.fragmentScreenSwitcher;
        }

        @Provides
        @DatedChecksScope ChangeGroupHelper provideFAKEGroupHelper(){
            //need for work of ChecksPresenter, don`t used on DatetChecksScreen
            return new ChangeGroupHelper();
        }
    }

    @DatedChecksScope
    @Component(dependencies = TeacherComponent.class, modules = DatedChecksScope.DatedChecksModule.class)
    interface DatedChecksComponent {

        void inject(DatedCheckActivity datedCheckActivity);

        ChecksScope.ChecksComponent checksComponent(ChecksScope.ChecksModule module);


    }
}
