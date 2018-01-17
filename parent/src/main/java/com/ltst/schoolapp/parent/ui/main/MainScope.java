package com.ltst.schoolapp.parent.ui.main;

import android.os.Bundle;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.main.chats.ChatsScope;
import com.ltst.schoolapp.parent.ui.main.checks.ChecksScope;
import com.ltst.schoolapp.parent.ui.main.events.EventsScope;
import com.ltst.schoolapp.parent.ui.main.feed.FeedScope;
import com.ltst.schoolapp.parent.ui.main.profile.ProfileScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface MainScope {

    @Module
    class MainModule {
        private final DialogProvider dialogProvider;
        private final FragmentScreenSwitcher fragmentScreenSwitcher;
        private final ChildInGroupHelper spinnerHelper;
        private final ActivityProvider activityProvider;

        public MainModule(DialogProvider dialogProvider,
                          FragmentScreenSwitcher fragmentScreenSwitcher,
                          ChildInGroupHelper spinnerHelper,
                          ActivityProvider activityProvider) {
            this.dialogProvider = dialogProvider;
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
            this.spinnerHelper = spinnerHelper;
            this.activityProvider = activityProvider;
        }

        @Provides
        @MainScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }

        @Provides
        @MainScope
        FragmentScreenSwitcher fragmentScreenSwitcher() {
            return this.fragmentScreenSwitcher;
        }
//
//        @Provides
//        @MainScope Calendar provideCalendar() {
//            return Calendar.getInstance();
//        }

        @Provides
        @MainScope Bundle provideEmptyBundle() {
            return new Bundle();
        }

        @Provides
        @MainScope
        ChildInGroupHelper provideSpinnerHelper() {
            return this.spinnerHelper;
        }

        @Provides
        @MainScope
        ActivityProvider provideActivityProvider(){
            return activityProvider;
        }
    }

    @MainScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = MainModule.class)
    interface MainComponent {

        void inject(MainActivity mainActivity);

        ProfileScope.ProfileComponent profileComponent(ProfileScope.ProfileModule module);

        FeedScope.FeedComponent feedComponent(FeedScope.FeedModule module);

        ChecksScope.FixComponent fixComponent(ChecksScope.FixModule module);

        EventsScope.EventsComponent eventsComponent(EventsScope.EventsModule module);

        ChatsScope.ChatsComponent chatsComponent(ChatsScope.ChatsModule module);
    }
}
