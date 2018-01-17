package com.ltst.schoolapp.teacher.ui.enter.start;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.preferences.BooleanPreference;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.preferences.qualifiers.NeedShowLogoutPopup;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.enter.code.CodeFragment;
import com.ltst.schoolapp.teacher.ui.enter.login.LoginFragment;
import com.ltst.schoolapp.teacher.ui.enter.registration.RegistrationFragment;

import javax.inject.Inject;

import rx.Subscription;

public class StartPresenter implements StartContract.Presenter {
    private final FragmentScreenSwitcher fragmentScreenSwitcher;
    private final StartContract.View view;
    private final DataService dataService;
    private Profile enterProfile;
    private final ActivityScreenSwitcher activitySwitcher;
    private final BooleanPreference needShowLogoutPopup;

    private Subscription checkProfileSubscription;

    @Inject
    public StartPresenter(FragmentScreenSwitcher fragmentScreenSwitcher,
                          StartContract.View view,
                          DataService dataService,
                          Profile enterProfile,
                          ActivityScreenSwitcher activitySwitcher,
                          @NeedShowLogoutPopup BooleanPreference needShowLogoutPopup) {
        this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        this.view = view;
        this.dataService = dataService;
        this.enterProfile = enterProfile;
        this.activitySwitcher = activitySwitcher;
        this.needShowLogoutPopup = needShowLogoutPopup;
    }




    @Override
    public void start() {
        if (needShowLogoutPopup.get()) {
            view.showLogoutFromServerPopup();
            needShowLogoutPopup.set(false);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void firstStart() {

    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void openCodeScreen() {
        fragmentScreenSwitcher.open(new CodeFragment.Screen());
    }

    @Override
    public void openRegistrationScreen() {
        fragmentScreenSwitcher.open(new RegistrationFragment.Screen());
    }

    @Override
    public void openLoginScreen() {
        fragmentScreenSwitcher.open(new LoginFragment.Screen());
    }


}
