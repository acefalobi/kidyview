package com.ltst.schoolapp.teacher.ui.activities.add.fragment;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AddPostScope {

    @AddPostScope
    @Subcomponent(modules = AddPostModule.class)
    interface AddPostComponent {
        void inject(AddPostFragment addPostFragment);
    }

    @Module
    class AddPostModule {
        private final AddPostContract.View view;
        private final PermissionsHandler permissionsHandler;

        public AddPostModule(AddPostContract.View view, PermissionsHandler permissionsHandler) {
            this.view = view;
            this.permissionsHandler = permissionsHandler;
        }

        @Provides
        @AddPostScope
        AddPostContract.View provideView() {
            return this.view;
        }

        @Provides
        @AddPostScope
        PermissionsHandler providePermissionHandler(){
            return this.permissionsHandler;
        }
    }
}
