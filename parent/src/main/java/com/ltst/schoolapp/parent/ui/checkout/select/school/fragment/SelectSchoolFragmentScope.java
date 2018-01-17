package com.ltst.schoolapp.parent.ui.checkout.select.school.fragment;


import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
public @interface SelectSchoolFragmentScope {

    @SelectSchoolFragmentScope
    @Subcomponent(modules = SelectSchoolFragmentModule.class)
    interface SelectSchoolFragmentComponent {

        void inject(SelectChildInSchoolFragment selectChildInSchoolFragment);
    }

    @Module
    class SelectSchoolFragmentModule {
    private final SelectChildInSchoolContract.View view;

        public SelectSchoolFragmentModule(SelectChildInSchoolContract.View view) {
            this.view = view;
        }

        @Provides
        @SelectSchoolFragmentScope
        SelectChildInSchoolContract.View provideView(){
            return view;
        }
    }
}
