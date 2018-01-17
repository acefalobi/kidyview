package com.ltst.schoolapp.teacher.ui.events.add.fragment;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AddEventScope {

    @AddEventScope
    @Subcomponent(modules = AddEventModule.class)
    interface AddEventComponent {
        void inject(AddEventFragment addEventFragment);
    }

    @Module
    class AddEventModule {
        private final AddEventContract.View view;
        private final PermissionsHandler permissionsHandler;

        public AddEventModule(AddEventContract.View view,
                              PermissionsHandler permissionsHandler) {
            this.view = view;
            this.permissionsHandler = permissionsHandler;
        }

        @Provides
        @AddEventScope
        AddEventContract.View provideView() {
            return this.view;
        }

        @Provides
        @AddEventScope
        PermissionsHandler providePermissionHandler(){
            return this.permissionsHandler;
        }
    }
}
