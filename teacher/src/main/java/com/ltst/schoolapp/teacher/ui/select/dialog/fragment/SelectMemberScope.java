package com.ltst.schoolapp.teacher.ui.select.dialog.fragment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface SelectMemberScope {

    @Module
    class SelectMemberModule{
        private final SelectMemberContract.View view;

        public SelectMemberModule(SelectMemberContract.View view) {
            this.view = view;
        }

        @Provides
        @SelectMemberScope
        SelectMemberContract.View provideView(){
            return view;
        }
    }

    @SelectMemberScope
    @Subcomponent(modules = SelectMemberScope.SelectMemberModule.class)
    interface SelectMemberComponent {

        void inject(SelectMemberFragment selectMemberFragment);
    }
}
