package com.ltst.schoolapp.teacher.ui.addchild;

import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.addchild.fragment.AddChildFragmentScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AddChildScope {

    @AddChildScope
    @Component (dependencies = TeacherComponent.class, modules = AddChildModule.class)
    interface TempComponent {
        void inject (AddChildActivity activity);

        AddChildFragmentScope.AddChildFragmentComponent addChildComponent(AddChildFragmentScope.AddChildFragmentModule module);
    }


    @Module
    class AddChildModule {
        private final ActivityProvider activityProvider;
        private final GalleryPictureLoader galleryPictureLoader;
        private final DialogProvider dialogProvider;
        private final int childId;

        public AddChildModule(ActivityProvider activityProvider, GalleryPictureLoader galleryPictureLoader,
                              DialogProvider dialogProvider, int childId) {
            this.activityProvider = activityProvider;
            this.galleryPictureLoader = galleryPictureLoader;
            this.dialogProvider = dialogProvider;
            this.childId = childId;
        }

        @Provides
        @AddChildScope
        ActivityProvider provideActivityProvider(){
            return this.activityProvider;
        }

        @Provides
        @AddChildScope
        GalleryPictureLoader provideGalleryPictureProvider(){
            return this.galleryPictureLoader;
        }

        @Provides
        @AddChildScope
        DialogProvider provideDialogProvider(){
            return this.dialogProvider;
        }

//        @Provides
//        @AddChildScope
//        int provideChildIdForEdit(){
//            return this.childId;
//        }
    }
}
