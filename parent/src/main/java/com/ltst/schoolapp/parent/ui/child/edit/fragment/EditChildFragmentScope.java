package com.ltst.schoolapp.parent.ui.child.edit.fragment;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EditChildFragmentScope {

    @Module
    class EditChildFragmentModule {
        private final EditChildContract.View view;
        private final PermissionsHandler permissionsHandler;

        public EditChildFragmentModule(EditChildContract.View view, PermissionsHandler permissionsHandler) {
            this.view = view;
            this.permissionsHandler = permissionsHandler;
        }

        @Provides
        @EditChildFragmentScope
        EditChildContract.View provideView(){
            return view;
        }

        @Provides
        @EditChildFragmentScope
        PermissionsHandler providePermissionHandler(){
            return this.permissionsHandler;
        }
    }

    @EditChildFragmentScope
    @Subcomponent(modules = EditChildFragmentScope.EditChildFragmentModule.class)
    interface EditChildFragmentComponent{

        void inject(EditChildFragment editChildFragment);
    }


}
