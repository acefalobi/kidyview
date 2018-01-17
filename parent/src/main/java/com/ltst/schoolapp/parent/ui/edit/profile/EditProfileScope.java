package com.ltst.schoolapp.parent.ui.edit.profile;

import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.edit.profile.fragment.EditProfileFragmentScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EditProfileScope {

    @Module
    class EditProfileModule {
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader galleryPictureLoader;

        public EditProfileModule(DialogProvider dialogProvider,

                                 GalleryPictureLoader galleryPictureLoader) {
            this.dialogProvider = dialogProvider;
            this.galleryPictureLoader = galleryPictureLoader;
        }

        @EditProfileScope
        @Provides DialogProvider provideDialogProvider() {
            return dialogProvider;
        }

        @EditProfileScope
        @Provides
        GalleryPictureLoader provideGalleryPictureLoader(){
            return this.galleryPictureLoader;
        }
    }

    @EditProfileScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = EditProfileModule.class)
    interface EditProfileComponent {

        void inject(EditProfileActivity editProfileActivity);

        EditProfileFragmentScope.EditProfileFragmentComponent
        editProfileComponent(EditProfileFragmentScope.EditProfileFragmentModule module);
    }


}
