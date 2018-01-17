package com.ltst.schoolapp.parent;

import com.layer.sdk.LayerClient;
import com.livetyping.utils.preferences.BooleanPreference;
import com.ltst.core.CoreComponent;
import com.ltst.core.data.preferences.qualifiers.IsFirstStart;
import com.ltst.core.firebase.PushNotificationCreator;
import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.util.SharingService;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.data.ParentDataModule;
import com.ltst.schoolapp.parent.firebase.message.FireBaseMessageComponent;
import com.ltst.schoolapp.parent.firebase.message.ParentPushNotificationCreator;
import com.ltst.schoolapp.parent.firebase.token.RefreshFirebaseTokenComponent;
import com.ltst.schoolapp.parent.receviers.ReceiverComponent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentScope {

    @ParentScope
    @Component(dependencies = CoreComponent.class, modules = {ParentAppModule.class, ParentDataModule.class})
    interface ParentComponent {

        void inject(ParentApplication parentApplication);

        DataService dataService();

        ActivityScreenSwitcher activitySwitcher();

        ParentApplication application();

        SharingService sharingService();

        ApplicationSwitcher applicationSwitcher();

        @IsFirstStart
        BooleanPreference isFirstStart();

        LayerClient layerClient(); //from LayerModule

        LayerNotificationsHelper layerNotificationHelper(); //from LayerModule

        ReceiverComponent receiverComponent();

        RefreshFirebaseTokenComponent refreshFBTokenComponent();

        FireBaseMessageComponent fireBaseMessageComponent();

//        PushNotificationCreator parentPushNotificationCreator();

    }

    @Module
    class ParentAppModule {

        private final ParentApplication application;

        public ParentAppModule(ParentApplication application) {
            this.application = application;
        }

        @ParentScope
        @Provides
        ParentApplication provideApplication() {
            return this.application;
        }

        @ParentScope
        @Provides
        ActivityScreenSwitcher provideActivitySwitcher() {
            return new ActivityScreenSwitcher();
        }

        @Provides
        @ParentScope
        SharingService provideSharingService(ParentApplication application) {
            return new SharingService(application);
        }

        @Provides
        @ParentScope
        ApplicationSwitcher provideApplicationSwitcher() {
            return new ApplicationSwitcher();
        }

        @Provides
        @ParentScope
        PushNotificationCreator providePushNotificationCreator(ParentApplication parentApplication){
            return new ParentPushNotificationCreator(parentApplication);
        }

    }
}
