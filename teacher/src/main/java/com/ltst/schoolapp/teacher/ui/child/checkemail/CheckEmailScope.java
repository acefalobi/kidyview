package com.ltst.schoolapp.teacher.ui.child.checkemail;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckEmailScope {

    @Module
    class CheckEmailModule {
        private final CheckEmailContract.View view;

        public CheckEmailModule(CheckEmailContract.View view) {
            this.view = view;
        }

        @Provides
        @CheckEmailScope
        CheckEmailContract.View provideView() {
            return view;
        }
    }

    @CheckEmailScope
    @Subcomponent(modules = CheckEmailScope.CheckEmailModule.class)
    interface CheckEmailComponent {

        void inject(CheckEmailFragment checkEmailFragment);
    }
}
