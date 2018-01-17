package com.ltst.schoolapp.teacher.ui.enter;

import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileScope;
import com.ltst.schoolapp.teacher.ui.enter.code.CodeScope;
import com.ltst.schoolapp.teacher.ui.enter.forgot.ForgotScope;
import com.ltst.schoolapp.teacher.ui.enter.login.LoginScope;
import com.ltst.schoolapp.teacher.ui.enter.newpass.NewPassScope;
import com.ltst.schoolapp.teacher.ui.enter.registration.RegistrationScope;
import com.ltst.schoolapp.teacher.ui.enter.start.StartScope;
import com.ltst.schoolapp.teacher.ui.enter.welcome.WelcomeScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;


@Scope
public @interface EnterScope {

    @EnterScope
    @Component(dependencies = {TeacherComponent.class}, modules = EnterModule.class)
    interface EnterComponent {
        void inject(EnterActivity activity);

        StartScope.StartComponent startComponent(StartScope.StartModule module);

        CodeScope.CodeComponent codeComponent(CodeScope.CodeModule module);

        LoginScope.LoginComponent loginComponent(LoginScope.LoginModule module);

        RegistrationScope.RegistrationComponent registrationComponent(RegistrationScope.RegistrationModule module);

        WelcomeScope.WelcomeComponent welcomeComponent(WelcomeScope.WelcomeModule module);

        EditProfileScope.EditProfileComponent profileComponent(EditProfileScope.EditProfileModule module);

        ForgotScope.ForgotComponent forgotComponent(ForgotScope.ForgotModule module);

        NewPassScope.NewPassComponent newPassComponent(NewPassScope.NewPassModule module);

    }

    @Module
    class EnterModule {
        private final FragmentScreenSwitcher fragmentSwitcher;
        private final DialogProvider dialogProvider;
        private final Profile enterProfile;
        private final GalleryPictureLoader pictureLoader;
        private EnterActivity.Screen.Params screenParams;

        public EnterModule(FragmentScreenSwitcher fragmentSwitcher,
                           DialogProvider dialogProvider,
                           Profile profile, GalleryPictureLoader galleryPictureLoader,
                           EnterActivity.Screen.Params screenParams) {
            this.fragmentSwitcher = fragmentSwitcher;
            this.dialogProvider = dialogProvider;
            this.enterProfile = profile;
            this.pictureLoader = galleryPictureLoader;
            this.screenParams = screenParams;
        }

        @Provides
        @EnterScope
        FragmentScreenSwitcher provideFragmentScreenSwitcher() {
            return this.fragmentSwitcher;
        }

        @Provides
        @EnterScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }

        @Provides
        @EnterScope
        Profile provideEnterProfile() {
            return this.enterProfile;
        }

        @Provides
        @EnterScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return this.pictureLoader;
        }

        @Provides
        @EnterScope
        EnterActivity.Screen.Params provideScreenParams() {
            screenParams = screenParams == null ?
                    new EnterActivity.Screen.Params(EnterActivity.Screen.Params.EnterFragment.NONE) : screenParams;
            return screenParams;
        }

//        @Provides
//        @Named(EditProfileFragment.SCREEN_MODE)
//        @EnterScope
//        int provideScreenMode() {
//            return EditProfileFragment.DEFAULT; // default EditProfile (edit and profile and school)
//        }
    }
}
