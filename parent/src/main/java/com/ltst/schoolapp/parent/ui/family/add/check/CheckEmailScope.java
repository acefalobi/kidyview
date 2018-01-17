package com.ltst.schoolapp.parent.ui.family.add.check;


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

        @CheckEmailScope
        @Provides
        CheckEmailContract.View provideView(){
            return view;
        }
    }

    @CheckEmailScope
    @Subcomponent(modules = CheckEmailModule.class)
    interface CheckEmailComponent {

        void inject(CheckEmailFragment checkEmailFragment);
    }
}
