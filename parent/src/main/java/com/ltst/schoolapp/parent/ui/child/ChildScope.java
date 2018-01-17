package com.ltst.schoolapp.parent.ui.child;

import android.os.Bundle;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.ui.child.edit.fragment.EditChildFragmentScope;
import com.ltst.schoolapp.parent.ui.child.view.ViewChildScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChildScope {

    @Module
    class ChildModule {

        //        private final ParentChild parentChild;
        private final Bundle screenParams;
        private final FragmentScreenSwitcher fragmentSwitcher;
        private final ActivityProvider activityProvider;
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader galleryPictureLoader;

        public ChildModule(Bundle screenParams,
                           FragmentScreenSwitcher fragmentSwitcher,
                           ActivityProvider activityProvider,
                           DialogProvider dialogProvider, GalleryPictureLoader galleryPictureLoader) {
            this.screenParams = screenParams;
            this.fragmentSwitcher = fragmentSwitcher;
            this.activityProvider = activityProvider;
            this.dialogProvider = dialogProvider;
            this.galleryPictureLoader = galleryPictureLoader;
        }

        @Provides
        @ChildScope
        ParentChild provideChild() {
            return this.screenParams.getParcelable(ChildActivity.Screen.KEY_ITEM_CHILD);
        }

        @Provides
        @ChildScope
        boolean canEditChild() {
            return screenParams.getBoolean(ChildActivity.Screen.KEY_CAN_EDIT);
        }

        @Provides
        @ChildScope
        FragmentScreenSwitcher provideFragmentSwitcher() {
            return fragmentSwitcher;
        }

        @Provides
        @ChildScope
        ActivityProvider provideActivityProvider() {
            return activityProvider;
        }

        @Provides
        @ChildScope
        DialogProvider provideDialogProvider() {
            return dialogProvider;
        }

        @Provides
        @ChildScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return galleryPictureLoader;
        }


    }

    @ChildScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = ChildModule.class)
    interface ChildComponent {

        void inject(ChildActivity activity);

        ViewChildScope.ViewChildComponent viewChildComponent(ViewChildScope.ViewChildModule module);

        EditChildFragmentScope.EditChildFragmentComponent editChildComponent
                (EditChildFragmentScope.EditChildFragmentModule module);


    }
}
