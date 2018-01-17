package com.ltst.schoolapp.parent.ui.enter.start;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.schoolapp.parent.ui.enter.login.LoginFragment;
import com.ltst.schoolapp.parent.ui.enter.registration.RegistrationFragment;

import javax.inject.Inject;

public class StartPresenter implements StartContract.Presenter {

    private final StartContract.View view;
    private final FragmentScreenSwitcher fragmentSwitcher;

    @Inject
    public StartPresenter(StartContract.View view,
                          FragmentScreenSwitcher fragmentSwitcher) {
        this.view = view;
        this.fragmentSwitcher = fragmentSwitcher;
    }

    @Override
    public void start() {

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
    public void goToLogin() {
        fragmentSwitcher.open(new LoginFragment.Screen());
    }

    @Override
    public void goToRegistration() {
        fragmentSwitcher.open(new RegistrationFragment.Screen());
    }
}
