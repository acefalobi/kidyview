package com.ltst.schoolapp.teacher.ui.main;


import android.os.Bundle;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.addchild.fragment.AddChildFragmentScope;
import com.ltst.schoolapp.teacher.ui.main.chats.ChatsScope;
import com.ltst.schoolapp.teacher.ui.main.checks.ChecksScope;
import com.ltst.schoolapp.teacher.ui.main.children.ChildrenScope;
import com.ltst.schoolapp.teacher.ui.main.feed.FeedScope;
import com.ltst.schoolapp.teacher.ui.main.profile.ProfileScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface MainScope {

    @MainScope
    @Component(dependencies = TeacherComponent.class, modules = MainModule.class)
    interface MainComponent {
        FeedScope.FeedComponent feedcomponent(FeedScope.FeedModule feedModule);

        ChildrenScope.ChildrenComponent childrenComponent(ChildrenScope.ChildrenModule childrenModule);

        AddChildFragmentScope.AddChildFragmentComponent addChildComponent(AddChildFragmentScope.AddChildFragmentModule module);

        ProfileScope.ProfileComponent addProfileComponent(ProfileScope.ProfileModule module);

        ChecksScope.ChecksComponent checksComponent(ChecksScope.ChecksModule module);

//        EventsScope.EventsComponent eventsFragment(EventsScope.EventsModule module);

        ChatsScope.Component chatsComponent (ChatsScope.ChatsModule module);

        void inject(MainActivity activity);
    }

    @Module
    class MainModule {
        private final FragmentScreenSwitcher fragmentScreenSwitcher;
        private final ActivityProvider activityProvider;
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader galleryPictureLoader;
        private final ChangeGroupHelper changeGroupHelper;

        public MainModule(FragmentScreenSwitcher fragmentScreenSwitcher, ActivityProvider activityProvider, DialogProvider dialogProvider, GalleryPictureLoader galleryPictureLoader, ChangeGroupHelper changeGroupHelper) {
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
            this.activityProvider = activityProvider;
            this.dialogProvider = dialogProvider;
            this.galleryPictureLoader = galleryPictureLoader;
            this.changeGroupHelper = changeGroupHelper;
        }

        @Provides
        @MainScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }

        @Provides
        @MainScope
        FragmentScreenSwitcher provideFragmentScreenSwitcher() {
            return this.fragmentScreenSwitcher;
        }

        @Provides
        @MainScope
        ActivityProvider provideActivityProvider() {
            return this.activityProvider;
        }

        @Provides
        @MainScope
        GalleryPictureLoader provideGallaryPictureProvider() {
            return this.galleryPictureLoader;
        }

        @Provides
        @MainScope
        Calendar provideCalendar() {
            return Calendar.getInstance();
        }

        @Provides
        @MainScope
        Bundle provideEmptyBundle(){ //provide empty bundle for ChecksPresenter. Other way -
            // provide bunle with date after filter of checks
            return new Bundle();
        }

        @Provides
        @MainScope
        ChangeGroupHelper provideChangeGroupListener(){
            return changeGroupHelper;
        }


    }
}
