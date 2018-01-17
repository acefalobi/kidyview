package com.ltst.schoolapp.teacher.ui.addchild.fragment;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AddChildFragmentScope {

    @AddChildFragmentScope
    @Subcomponent(modules = AddChildFragmentModule.class)
    interface AddChildFragmentComponent {

        void inject(AddChildFragment fragment);
    }

    @Module
    class AddChildFragmentModule {

        private final AddChildContract.View view;
        private final int childId;
        private final PermissionsHandler permissionsHandler;

        public AddChildFragmentModule(AddChildContract.View view, PermissionsHandler permissionsHandler,
                                      int childId) {
            this.view = view;
            this.childId = childId;
            this.permissionsHandler = permissionsHandler;
        }

        @Provides
        @AddChildFragmentScope
        AddChildContract.View provideAddChildView() {
            return this.view;
        }

        @Provides
        @AddChildFragmentScope
        int provideChildId() {
            return this.childId;
        }

        @Provides
        @AddChildFragmentScope
        PermissionsHandler providePermissionsHelper() {
            return this.permissionsHandler;
        }
    }
}
