package com.ltst.schoolapp;


import com.ltst.core.firebase.PushNotificationCreator;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.util.SharingService;
import com.ltst.schoolapp.teacher.firebase.message.TeacherPushNotificationCreator;

import dagger.Module;
import dagger.Provides;

@Module
public class TeacherAppModule {
    private final TeacherApplication application;


    public TeacherAppModule(TeacherApplication application) {
        this.application = application;
    }

    @Provides
    @TeacherScope
    TeacherApplication provideTeacherApplication() {
        return application;
    }


    @Provides
    @TeacherScope
    ActivityScreenSwitcher provideActivityScreenSwitcher() {
        return new ActivityScreenSwitcher();
    }

    @Provides
    @TeacherScope
    ApplicationSwitcher provideApplicationSwitcher() {
        return new ApplicationSwitcher();
    }


    @Provides
    @TeacherScope
    SharingService provideSharingService(TeacherApplication application) {
        return new SharingService(application);
    }

    @Provides
    @TeacherScope
    PushNotificationCreator providePushNotificationCreator(TeacherApplication application) {
        return new TeacherPushNotificationCreator(application);
    }

}
