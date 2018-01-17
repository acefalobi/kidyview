package com.ltst.schoolapp.teacher.ui.settings.editgroup.fragment;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EditGroupScope {

    @EditGroupScope
    @Subcomponent(modules = EditGroupModule.class)
    interface EditGroupComponent {
        void inject(EditGroupFragment editGroupFragment);
    }

    @Module
    class EditGroupModule {
        private final EditGroupContract.View view;
        private final PermissionsHandler permissionsHandler;

        public EditGroupModule(EditGroupContract.View view, PermissionsHandler permissionsHandler) {
            this.view = view;
            this.permissionsHandler = permissionsHandler;
        }

        @Provides
        @EditGroupScope
        EditGroupContract.View provideEditProfileView() {
            return this.view;
        }

        @Provides
        @EditGroupScope
        PermissionsHandler providePermissionsHandler(){
            return this.permissionsHandler;
        }
    }
}
