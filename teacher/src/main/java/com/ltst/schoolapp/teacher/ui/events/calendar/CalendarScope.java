package com.ltst.schoolapp.teacher.ui.events.calendar;


import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.events.calendar.fragment.EventsScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface CalendarScope {

    @CalendarScope
    @Component(dependencies = TeacherComponent.class, modules = CalendarModule.class)
    interface CalendarComponent {
        void inject(CalendarActivity activity);

        EventsScope.EventsComponent eventsFragment(EventsScope.EventsModule eventsModule);
    }

    @Module
    class CalendarModule {
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader galleryPictureLoader;
        private final FragmentScreenSwitcher fragmentScreenSwitcher;
        private final ActivityProvider activityProvider;

        public CalendarModule(DialogProvider dialogProvider,
                              GalleryPictureLoader galleryPictureLoader,
                              FragmentScreenSwitcher fragmentScreenSwitcher,
                              ActivityProvider activityProvider) {
            this.dialogProvider = dialogProvider;
            this.galleryPictureLoader = galleryPictureLoader;
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
            this.activityProvider = activityProvider;
        }

        @Provides
        @CalendarScope
        DialogProvider provideDialogProvider() {
            return dialogProvider;
        }

        @Provides
        @CalendarScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return galleryPictureLoader;
        }

        @Provides
        @CalendarScope
        FragmentScreenSwitcher provideFragmentScreenSwithcer() {
            return fragmentScreenSwitcher;
        }

        @Provides
        @CalendarScope
        ActivityProvider provideActivityProvider(){
            return activityProvider;
        }
    }
}
