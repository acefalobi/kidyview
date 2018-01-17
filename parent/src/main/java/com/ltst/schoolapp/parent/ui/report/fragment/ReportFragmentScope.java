package com.ltst.schoolapp.parent.ui.report.fragment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportFragmentScope {

    @Module
    class ReportFragmentModule {
        private final ReportContract.View view;

        public ReportFragmentModule(ReportContract.View view) {
            this.view = view;
        }

        @Provides
        @ReportFragmentScope
        ReportContract.View provideView() {
            return view;
        }
    }

    @ReportFragmentScope
    @Subcomponent(modules = ReportFragmentScope.ReportFragmentModule.class)
    interface ReportFragmentComponent {

        void inject(ReportFragment reportFragment);
    }
}
