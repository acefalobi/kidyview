package com.ltst.schoolapp;

import com.layer.sdk.LayerClient;
import com.livetyping.utils.preferences.BooleanPreference;
import com.ltst.core.CoreComponent;
import com.ltst.core.data.preferences.qualifiers.IsAdmin;
import com.ltst.core.data.preferences.qualifiers.NeedShowLogoutPopup;
import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.util.SharingService;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.data.TeacherDataModule;
import com.ltst.schoolapp.teacher.firebase.message.FireBaseMessageComponent;
import com.ltst.schoolapp.teacher.firebase.message.TeacherFireBaseMessageService;
import com.ltst.schoolapp.teacher.firebase.token.RefreshFBTokenComponent;
import com.ltst.schoolapp.teacher.receivers.ReceiverComponent;

import dagger.Component;

@TeacherScope
@Component(dependencies = CoreComponent.class, modules = {TeacherAppModule.class, TeacherDataModule.class})
public interface TeacherComponent {

    DataService dataService();

    ApplicationSwitcher applicationSwitcher();

    TeacherApplication teacherApplication();

    SharingService sharingService();

    ActivityScreenSwitcher activityScreenSwitcher();

    @NeedShowLogoutPopup BooleanPreference needShowLogoutPopup();

    @IsAdmin BooleanPreference isAdmin();

    FireBaseMessageComponent fireBaseMessageComponent();




    void inject(TeacherApplication teacherApplication);

    LayerClient layerClient(); // from CoreComponent

    LayerNotificationsHelper layerNotificationHelper(); // from CoreComponent

    ReceiverComponent receiverComponent(ReceiverComponent.ReceiverModule module);

    RefreshFBTokenComponent refreshFireBaseTokenComponent();
}
