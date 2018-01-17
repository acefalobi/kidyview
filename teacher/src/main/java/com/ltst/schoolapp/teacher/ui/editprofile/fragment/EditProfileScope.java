package com.ltst.schoolapp.teacher.ui.editprofile.fragment;

import android.support.annotation.IntDef;

import com.ltst.core.permission.PermissionsHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Named;
import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EditProfileScope {

    @EditProfileScope
    @Subcomponent(modules = EditProfileModule.class)
    interface EditProfileComponent {

        void inject(EditProfileFragment editProfileFragment);
    }

    @Module
    class EditProfileModule {

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({FROM_ENTER, FROM_PROFILE, FROM_SCHOOL})
        @interface FromScreen {

        }

        public static final int FROM_ENTER = 0;
        public static final int FROM_PROFILE = 1;
        public static final int FROM_SCHOOL = 2;
        public static final String FROM_SCREEN = "from.screen";
        private final EditProfileContract.View view;
        private final PermissionsHandler permissionsHandler;
        private final int screenMode;
        private int fromScreen;

        public EditProfileModule(EditProfileContract.View view,
                                 PermissionsHandler permissionsHandler,
                                 @FromScreen int fromScreen,
                                 int screenMode) {
            this.view = view;
            this.fromScreen = fromScreen;
            this.permissionsHandler = permissionsHandler;
            this.screenMode = screenMode;
        }

        @Provides
        @EditProfileScope
        EditProfileContract.View provideEditProfileView() {
            return this.view;
        }

        @Provides
        @Named(FROM_SCREEN)
        @EditProfileScope
        int provideFromScreen() {
            return fromScreen;
        }

        @Provides
        @EditProfileScope
        PermissionsHandler providePermissionHandler() {
            return this.permissionsHandler;
        }

        @Provides
        @EditProfileScope
        @Named(EditProfileFragment.SCREEN_MODE)
        int provideScreenMode() {
            return screenMode;
        }
    }
}
