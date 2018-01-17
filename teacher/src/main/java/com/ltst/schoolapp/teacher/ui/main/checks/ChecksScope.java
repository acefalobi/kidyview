package com.ltst.schoolapp.teacher.ui.main.checks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChecksScope {

    @ChecksScope
    @Subcomponent(modules = ChecksModule.class)
    interface ChecksComponent {
        void inject(ChecksFragment fragment);
    }

    @Module
    class ChecksModule {
        private final ChecksContract.View view;

        public ChecksModule(ChecksContract.View view) {
            this.view = view;
        }

        @Provides
        @ChecksScope
        ChecksContract.View provideCheckView() {
            return this.view;
        }
    }
}
