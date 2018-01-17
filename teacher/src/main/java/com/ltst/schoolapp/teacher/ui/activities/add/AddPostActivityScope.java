package com.ltst.schoolapp.teacher.ui.activities.add;

import android.os.Bundle;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.activities.add.fragment.AddPostScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface AddPostActivityScope {

    @AddPostActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {AddPostActivityModule.class})
    interface AddPostActivityComponent {
        void inject(AddPostActivity activity);

        AddPostScope.AddPostComponent addPostComponent(AddPostScope.AddPostModule module);
    }

    @Module
    public class AddPostActivityModule {
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader galleryPictureLoader;
        private final Bundle screenParams;

        public AddPostActivityModule(CoreActivity coreActivity, Bundle screenParams) {
            this.screenParams = screenParams;
            this.dialogProvider = coreActivity.getDialogProvider();
            this.galleryPictureLoader = new GalleryPictureLoader(coreActivity);
        }

        @Provides
        Bundle provideScreenParams(){
            return screenParams;
        }

        @Provides
        DialogProvider provideDialogProvider() {
            return dialogProvider;
        }

        @Provides
        GalleryPictureLoader provideGalleryPictureLoader() {
            return galleryPictureLoader;
        }
    }
}
