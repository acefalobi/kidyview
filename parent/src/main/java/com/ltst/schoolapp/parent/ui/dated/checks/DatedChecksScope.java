package com.ltst.schoolapp.parent.ui.dated.checks;

import android.os.Bundle;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.main.ChildInGroupHelper;
import com.ltst.schoolapp.parent.ui.main.checks.ChecksScope;

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
        private final DialogProvider dialogProvider;

        public DatedChecksModule(Bundle screenParams, FragmentScreenSwitcher fragmentScreenSwitcher, DialogProvider dialogProvider) {
            this.screenParams = screenParams;
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
            this.dialogProvider = dialogProvider;
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
        @DatedChecksScope DialogProvider provideDialogProvider(){
            return this.dialogProvider;
        }

        @Provides
        @DatedChecksScope ChildInGroupHelper provideEmptySippenrHelper(){
            return new ChildInGroupHelper(); // for DatedCheckActivity
        }
    }

    @DatedChecksScope
    @Component(dependencies = ParentScope.ParentComponent.class,
            modules = DatedChecksScope.DatedChecksModule.class)
    interface DatedChecksComponent {

        void inject(DatedCheckActivity datedCheckActivity);

        ChecksScope.FixComponent checksComponent(ChecksScope.FixModule module);


    }
}
