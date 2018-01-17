package com.ltst.schoolapp.teacher.ui.school.fragment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SchoolFragmentScope {

    @Module
    class SchoolFragmentModule {
        private  final SchoolContract.View view;

        public SchoolFragmentModule(SchoolContract.View view) {
            this.view = view;
        }

        @Provides
        @SchoolFragmentScope
        SchoolContract.View provideView(){
            return view;
        }
    }

    @SchoolFragmentScope
    @Subcomponent (modules = SchoolFragmentScope.SchoolFragmentModule.class)
    interface SchoolFragmentComponent {
        void inject (SchoolFragment fragment);
    }
}
