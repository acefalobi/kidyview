package com.ltst.schoolapp.teacher.ui.enter.welcome;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment;

import javax.inject.Inject;

public class WelcomePresenter implements WelcomeContract.Presenter {

    private final FragmentScreenSwitcher fragmentSwitcher;

    @Inject
    public WelcomePresenter(FragmentScreenSwitcher fragmentSwitcher) {
        this.fragmentSwitcher = fragmentSwitcher;
    }

    @Override
    public void nextScreen() {
        fragmentSwitcher.open(new EditProfileFragment.Screen(EditProfileFragment.DEFAULT));
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
}
