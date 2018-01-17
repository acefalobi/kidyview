package com.ltst.schoolapp.parent.ui.school.info;


import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.school.info.fragment.SchoolInfoScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface SchoolScope {

    @Module
    class SchoolModule {
        private final DialogProvider dialogProvider;

        public SchoolModule(DialogProvider dialogProvider) {
            this.dialogProvider = dialogProvider;
        }

        @Provides
        @SchoolScope
        DialogProvider provideDialogProvider(){
            return dialogProvider;
        }
    }

    @SchoolScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = SchoolScope.SchoolModule.class)
    interface SchoolComponent {


        void inject(SchoolActivity schoolActivity);

        SchoolInfoScope.SchoolInfoComponent infoComponent(SchoolInfoScope.InfoModule module);
    }
}
