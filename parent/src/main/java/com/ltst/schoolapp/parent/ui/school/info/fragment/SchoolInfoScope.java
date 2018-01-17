package com.ltst.schoolapp.parent.ui.school.info.fragment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SchoolInfoScope {

    @Module
    class InfoModule {
        private final SchoolInfoContract.View view;

        public InfoModule(SchoolInfoContract.View view) {
            this.view = view;
        }

        @Provides
        @SchoolInfoScope
        SchoolInfoContract.View provideView() {
            return view;
        }
    }

    @SchoolInfoScope
    @Subcomponent(modules = SchoolInfoScope.InfoModule.class)
    interface SchoolInfoComponent {

        void inject(SchoolInfoFragment schoolInfoFragment);
    }
}
