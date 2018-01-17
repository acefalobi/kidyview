package com.ltst.schoolapp.teacher.ui.child;

import com.ltst.core.data.model.Child;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.child.addmember.AddMemberScope;
import com.ltst.schoolapp.teacher.ui.child.checkemail.CheckEmailScope;
import com.ltst.schoolapp.teacher.ui.child.family.FamilyScope;
import com.ltst.schoolapp.teacher.ui.child.viewchild.ViewChildScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChildScope {

    @ChildScope
    @Component(dependencies = TeacherComponent.class, modules = ChildModule.class)
    interface ChildComponent {
        void inject(ChildActivity activity);

        ViewChildScope.ViewChildComponent viewChildComponent(ViewChildScope.ViewChildModule module);

        FamilyScope.FamilyComponent familyComponent(FamilyScope.FamilyModule module);

        AddMemberScope.AddMemberComponent addMemberComponent(AddMemberScope.AddMemberModule module);

        CheckEmailScope.CheckEmailComponent checkEmailComponent(CheckEmailScope.CheckEmailModule module);
    }

    @Module
    class ChildModule {

        private final FragmentScreenSwitcher fragmentSwitcher;
        private final ActivityProvider activityProvider;
        private final DialogProvider dialogProvider;
        private final Child child;
        private final GalleryPictureLoader galleryPictureLoader;
        private long familyMemberId; // for open screen from notification for family request


        ChildModule(Child child, long familyMemberId, FragmentScreenSwitcher fragmentSwitcher,
                    ActivityProvider activityProvider,
                    DialogProvider dialogProvider, GalleryPictureLoader galleryPictureLoader) {
            this.fragmentSwitcher = fragmentSwitcher;
            this.activityProvider = activityProvider;
            this.dialogProvider = dialogProvider;
            this.child = child;
            this.galleryPictureLoader = galleryPictureLoader;
            this.familyMemberId = familyMemberId;
        }

        @Provides
        @ChildScope
        FragmentScreenSwitcher provdeiFragmentSwitcher() {
            return this.fragmentSwitcher;
        }

        @Provides
        @ChildScope
        ActivityProvider provideActivityProvider() {
            return this.activityProvider;
        }

        @Provides
        @ChildScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }

        @Provides
        @ChildScope
        Child provideChild() {
            return this.child;
        }

        @Provides
        @ChildScope
        long provideFamilyRequestMemberId(){
            return this.familyMemberId;
        }

        @Provides
        @ChildScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return this.galleryPictureLoader;
        }
    }
}
