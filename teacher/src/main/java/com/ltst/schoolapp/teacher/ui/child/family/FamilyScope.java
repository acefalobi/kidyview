package com.ltst.schoolapp.teacher.ui.child.family;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FamilyScope {

    @FamilyScope
    @Subcomponent(modules = FamilyModule.class)
    interface FamilyComponent {

        void inject(FamilyFragment familyFragment);
    }

    @Module
    class FamilyModule {
        private final FamilyContract.View view;

        public FamilyModule(FamilyContract.View view) {
            this.view = view;
        }

        @Provides
        @FamilyScope
        FamilyContract.View provideFamilyView() {
            return this.view;
        }
    }
}
