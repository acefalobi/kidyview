package com.ltst.schoolapp.parent.ui.edit.profile.fragment;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EditProfileFragmentScope {

    @Module
    class EditProfileFragmentModule {
        private final EditProfileContract.View view;
        private final PermissionsHandler permissionsHandler;

        public EditProfileFragmentModule(EditProfileContract.View view,
                                         PermissionsHandler permissionsHandler) {
            this.view = view;
            this.permissionsHandler = permissionsHandler;
        }

        @Provides
        @EditProfileFragmentScope EditProfileContract.View provideView() {
            return this.view;
        }

        @Provides
        @EditProfileFragmentScope PermissionsHandler providePermissionHandler(){
            return this.permissionsHandler;
        }
    }

    @EditProfileFragmentScope
    @Subcomponent(modules = EditProfileFragmentScope.EditProfileFragmentModule.class)
    interface EditProfileFragmentComponent {

        void inject(EditProfileFragment editProfileFragment);
    }
}
