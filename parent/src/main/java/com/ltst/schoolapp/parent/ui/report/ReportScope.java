package com.ltst.schoolapp.parent.ui.report;

import android.os.Bundle;

import com.ltst.core.data.model.Post;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.report.fragment.ReportFragmentScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface ReportScope {

    @Module
    class ReportModule {
        private final Bundle screenParams;
        private final FragmentScreenSwitcher fragmentScreenSwitcher;

        public ReportModule(Bundle screenParams, FragmentScreenSwitcher fragmentScreenSwitcher) {
            this.screenParams = screenParams;
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        }

        @Provides
        @ReportScope
        Post provideReportPost() {
            return ((Post) screenParams.getParcelable(ReportActivity.Screen.KEY_REPORT_POST));
        }

        @Provides
        @ReportScope
        FragmentScreenSwitcher provideFragmentSwitcher() {
            return fragmentScreenSwitcher;
        }
    }

    @ReportScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = ReportModule.class)
    interface ReportComponent {

        void inject(ReportActivity reportActivity);

        ReportFragmentScope.ReportFragmentComponent subComponent(ReportFragmentScope.ReportFragmentModule module);
    }
}
