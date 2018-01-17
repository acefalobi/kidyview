package com.ltst.schoolapp.teacher.ui.editprofile;

import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface EditProfileActivityScope {

    @EditProfileActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = EditProfileActivityModule.class)
    interface EditProfileActivityComponent {
        void inject(EditProfileActivity activity);

        EditProfileScope.EditProfileComponent profileComponent(EditProfileScope.EditProfileModule module);
    }

    @Module
    class EditProfileActivityModule {
        private final FragmentScreenSwitcher fragmentSwitcher;
        private final DialogProvider dialogProvider;
        private Profile enterProfile;
        private int screenMode;
        private final GalleryPictureLoader pictureLoader;

        public EditProfileActivityModule(FragmentScreenSwitcher fragmentSwitcher,
                                         DialogProvider dialogProvider,
                                         Profile profile,
                                         int screenMode,
                                         GalleryPictureLoader galleryPictureLoader) {
            this.fragmentSwitcher = fragmentSwitcher;
            this.dialogProvider = dialogProvider;
            this.enterProfile = profile;
            this.pictureLoader = galleryPictureLoader;
            this.screenMode = screenMode;
        }

        @Provides
        @EditProfileActivityScope
        FragmentScreenSwitcher provideFragmentScreenSwitcher() {
            return this.fragmentSwitcher;
        }

        @Provides
        @EditProfileActivityScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }

        @Provides
        @EditProfileActivityScope
        Profile provideEnterProfile() {
            return this.enterProfile;
        }

        @Provides
        @EditProfileActivityScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return this.pictureLoader;
        }

//        @Provides
//        @Named(EditProfileFragment.SCREEN_MODE)
//        @EditProfileScope int provideScreenMode() {
//            return screenMode;
//        }

    }
}
