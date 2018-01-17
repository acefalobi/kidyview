package com.ltst.schoolapp.teacher.ui.events.add;

import android.os.Bundle;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.events.add.fragment.AddEventScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface AddEventActivityScope {

    @AddEventActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {AddEventActivityModule.class})
    interface AddEventActivityComponent {
        void inject(AddEventActivity activity);

        AddEventScope.AddEventComponent addPostComponent(AddEventScope.AddEventModule module);
    }

    @Module
    class AddEventActivityModule {
        private DialogProvider dialogProvider;
        private GalleryPictureLoader galleryPictureLoader;
        private FragmentScreenSwitcher fragmentScreenSwitcher;
        private Bundle screenParams;

        public AddEventActivityModule(CoreActivity coreActivity, Bundle screenParams) {
            this.dialogProvider = coreActivity.getDialogProvider();
            this.galleryPictureLoader = new GalleryPictureLoader(coreActivity);
            this.fragmentScreenSwitcher = coreActivity.getFragmentScreenSwitcher();
            this.screenParams = screenParams;

        }

        @Provides
        DialogProvider provideDialogProvider() {
            return dialogProvider;
        }

        @Provides
        GalleryPictureLoader provideGalleryPictureLoader() {
            return galleryPictureLoader;
        }

        @Provides
        FragmentScreenSwitcher provideFragmentScreenSwitcher() {
            return fragmentScreenSwitcher;
        }

        @Provides
        Bundle provideScreenParams() {
            return this.screenParams;
        }

    }
}
