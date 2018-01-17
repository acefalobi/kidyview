package com.ltst.schoolapp.teacher.ui.activities.select.person.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectPersonScope {

    @SelectPersonScope
    @Subcomponent(modules = SelectPersonModule.class)
    interface SelectPersonComponent {
        void inject(SelectPersonFragment selectPersonFragment);
    }

    @Module
    class SelectPersonModule {
        private final SelectPersonContract.View view;

        public SelectPersonModule(SelectPersonContract.View view) {
            this.view = view;
        }

        @Provides
        @SelectPersonScope
        SelectPersonContract.View provideEditProfileView() {
            return this.view;
        }
    }
}
