package com.ltst.schoolapp.parent.ui.family.add;

import android.os.Bundle;

import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.family.add.check.CheckEmailScope;
import com.ltst.schoolapp.parent.ui.family.add.request.RequestScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AddMemberScope {

    @Module
    class AddMemberModule {

        private final Bundle screenParams;
        private final ActivityProvider activityProvider;
        private final GalleryPictureLoader galleryPictureLoader;
        private final DialogProvider dialogProvider;
        private final FragmentScreenSwitcher fragmentScreenSwitcher;

        public AddMemberModule(Bundle screenParams,
                               ActivityProvider activityProvider,
                               GalleryPictureLoader galleryPictureLoader,
                               DialogProvider dialogProvider,
                               FragmentScreenSwitcher fragmentScreenSwitcher) {
            this.screenParams = screenParams;
            this.activityProvider = activityProvider;
            this.galleryPictureLoader = galleryPictureLoader;
            this.dialogProvider = dialogProvider;
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        }

        @Provides
        @AddMemberScope
        Bundle provideScreenParams() {
            return this.screenParams;
        }

        @Provides
        @AddMemberScope
        ActivityProvider provideActivityProvider() {
            return this.activityProvider;
        }

        @Provides
        @AddMemberScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return this.galleryPictureLoader;
        }

        @Provides
        @AddMemberScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }

        @Provides
        @AddMemberScope
        FragmentScreenSwitcher provideFragmentSwitcher() {
            return fragmentScreenSwitcher;
        }

        @Provides
        @AddMemberScope
        Member provideMemberForRequest() {
            return new Member();
        }
    }

    @AddMemberScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = AddMemberModule.class)
    interface AddMemberComponent {

        void inject(AddMemberActivity addMemberActivity);

        RequestScope.RequestComponent fragmentComponent(RequestScope.RequestModule module);

        CheckEmailScope.CheckEmailComponent checkEmailComponent(CheckEmailScope.CheckEmailModule module);

    }
}
