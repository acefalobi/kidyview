package com.ltst.schoolapp.teacher.ui.checks.single.check.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleCheckScope {

    @SingleCheckScope
    @Subcomponent(modules = SingleCheckModule.class)
    interface SingleCheckComponent {
        void inject(SingleCheckFragment singleCheckFragment);
    }

    @Module
    class SingleCheckModule {
        private final SingleCheckContract.View view;

        public SingleCheckModule(SingleCheckContract.View view) {
            this.view = view;
        }

        @Provides
        @SingleCheckScope
        SingleCheckContract.View provideEditProfileView() {
            return this.view;
        }
    }
}
