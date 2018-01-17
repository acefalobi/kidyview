package com.ltst.schoolapp.parent.ui.family.add.request;

import android.os.Bundle;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface RequestScope {

    @Module
    class RequestModule {
        private final RequestContract.View view;
        private final PermissionsHandler permissionsHandler;
        private final int screenStatus;

        public RequestModule(RequestContract.View view,
                             PermissionsHandler permissionsHandler,
                             Bundle screenParams) {
            this.view = view;
            this.permissionsHandler = permissionsHandler;
            this.screenStatus = screenParams.getInt(RequestFragment.Screen.KEY_SCREEN_STATUS);
        }

        @Provides
        @RequestScope
        RequestContract.View provideView() {
            return this.view;
        }

        @Provides
        @RequestScope
        PermissionsHandler providePermissionHandler() {
            return this.permissionsHandler;
        }

        @Provides
        @RequestScope
        int provideScreenStatus(){
            return screenStatus;
        }
    }

    @RequestScope
    @Subcomponent(modules = RequestScope.RequestModule.class)
    interface RequestComponent {

        void inject(RequestFragment requestFragment);
    }

}
