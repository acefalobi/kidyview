package com.ltst.schoolapp.parent.ui.checkout.select.school;


import android.os.Bundle;

import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.checkout.select.school.fragment.SelectSchoolFragmentScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface SelectSchoolScope {

    @SelectSchoolScope
    @Component(dependencies = ParentScope.ParentComponent.class,modules = SelectSchoolModule.class)
    interface SelectSchoolComponent {
        void inject(SelectChildInSchoolActivity activity);
        SelectSchoolFragmentScope.SelectSchoolFragmentComponent
        sekectSchoolFragmentComponent(SelectSchoolFragmentScope.SelectSchoolFragmentModule module);
    }

    @Module
    class SelectSchoolModule {
        private final Bundle screenParams;

        public SelectSchoolModule(Bundle screenParams) {
            this.screenParams = screenParams;
        }

        @Provides
        @SelectSchoolScope
        Bundle provideScreenParams() {
            return this.screenParams;
        }
    }


}
