package com.ltst.schoolapp.parent.ui.child.edit;

import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.ui.child.edit.fragment.EditChildFragmentScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EditChildScope {

    @Module
    class EditChildModule {

        private final ParentChild child;
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader galleryPictureLoader;
        private final ActivityProvider activityProvider;

        public EditChildModule(ParentChild child,
                               DialogProvider dialogProvider,
                               GalleryPictureLoader galleryPictureLoader,
                               ActivityProvider activityProvider) {
            this.child = child;
            this.dialogProvider = dialogProvider;
            this.galleryPictureLoader = galleryPictureLoader;
            this.activityProvider = activityProvider;
        }

        @Provides
        @EditChildScope
        ParentChild provideEditedChild() {
            return child;
        }

        @Provides
        @EditChildScope
        DialogProvider provideDialogProvider() {
            return dialogProvider;
        }

        @Provides
        @EditChildScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return galleryPictureLoader;
        }

        @Provides
        @EditChildScope
        ActivityProvider provideActivityProvider(){
            return activityProvider;
        }
    }

    @EditChildScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = EditChildModule.class)
    interface EditChildComponent {

        void inject(EditChildActivity editChildActivity);

        EditChildFragmentScope.EditChildFragmentComponent editChildComponent
                (EditChildFragmentScope.EditChildFragmentModule module);



    }
}
