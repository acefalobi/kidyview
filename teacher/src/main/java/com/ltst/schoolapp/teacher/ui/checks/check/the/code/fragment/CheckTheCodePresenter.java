package com.ltst.schoolapp.teacher.ui.checks.check.the.code.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.check.the.code.CheckTheCodeActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivity;

import javax.inject.Inject;

public class CheckTheCodePresenter implements CheckTheCodeContract.Presenter {
    private final CheckTheCodeContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final String selectedGroupTitle;

    @Inject
    public CheckTheCodePresenter(CheckTheCodeContract.View view,
                                 ActivityScreenSwitcher screenSwitcher,
                                 Bundle screenParams) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.selectedGroupTitle = screenParams.getString(CheckTheCodeActivity.Screen.KEY_SELECTED_GROUP_TITLE);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  START SCREEN  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
    }

    @Override
    public void start() {
        initToolbar();
        view.bindGroupTitle(selectedGroupTitle);
        view.bindListeners(onCheckInClick(), onCheckOutClick());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  TOOLBAR  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void initToolbar() {
        view.initToolbar(R.drawable.ic_arrow_back_white_24dp, v -> {
            screenSwitcher.goBack();
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  BUTTONS  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener onCheckInClick() {
        return onCheckInClick -> openCheckSelectChildActivityCheckIn();
    }

    private View.OnClickListener onCheckOutClick() {
        return onCheckOutClick -> openCheckSelectChildActivityCheckOut();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////   NAVIGATION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void openCheckSelectChildActivityCheckIn() {
        screenSwitcher.open(new ChecksSelectChildActivity.Screen(true));
    }

    private void openCheckSelectChildActivityCheckOut() {
        screenSwitcher.open(new ChecksSelectChildActivity.Screen(false));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  LIFECYCLE  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
    }

    @Override
    public void stop() {
    }

}
