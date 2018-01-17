package com.ltst.schoolapp.teacher.ui.child.addmember;

import android.os.Bundle;
import android.support.annotation.IntDef;

import com.ltst.core.data.model.Member;
import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AddMemberScope {

    @AddMemberScope
    @Subcomponent(modules = AddMemberModule.class)
    interface AddMemberComponent {

        void inject(AddMemberFragment fragment);
    }

    @Module
    class AddMemberModule {

        private final AddMemberContract.View view;
        private final PermissionsHandler permissionsHandler;
        private final Bundle screenParams;

        public AddMemberModule(AddMemberContract.View view,
                               PermissionsHandler permissionsHandler,
                               Bundle screenParams) {
            this.view = view;
            this.permissionsHandler = permissionsHandler;
            this.screenParams = screenParams;
        }

        @Provides
        @AddMemberScope
        AddMemberContract.View provideAddMemberView() {
            return this.view;
        }

        @Provides
        @AddMemberScope
        PermissionsHandler providePermissionHandler() {
            return this.permissionsHandler;
        }

        @Provides
        @AddMemberScope Member provideMember() {
            return ((Member) screenParams.getParcelable(AddMemberFragment.Screen.KEY_FAMILY_MEMBER));
        }

        @Provides
        @AddMemberScope
        int provideScreenMode() {
            return screenParams.getInt(AddMemberFragment.Screen.KEY_SCREEN_MODE);
        }
    }

    int SCREEN_MODE_CREATE = 0;
    int SCREEN_MODE_EXIST = 1;

    @IntDef({SCREEN_MODE_CREATE, SCREEN_MODE_EXIST})
    @interface AddMEmberScreenMode {
    }
}
