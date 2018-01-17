package com.ltst.schoolapp.parent.ui.main.checks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChecksScope {

    @Module
    class FixModule {

        private final ChecksContract.View view;

        public FixModule(ChecksContract.View view) {
            this.view = view;
        }

        @Provides
        @ChecksScope
        ChecksContract.View provideView() {
            return view;
        }
    }

    @ChecksScope
    @Subcomponent(modules = FixModule.class)
    interface FixComponent {

        void inject(ChecksFragment checksFragment);
    }
}
