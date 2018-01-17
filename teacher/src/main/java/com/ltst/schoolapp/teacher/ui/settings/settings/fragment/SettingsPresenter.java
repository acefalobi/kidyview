package com.ltst.schoolapp.teacher.ui.settings.settings.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.enter.EnterActivity;
import com.ltst.schoolapp.teacher.ui.settings.changepassword.ChangePasswordActivity;
import com.ltst.schoolapp.teacher.ui.settings.editgroup.EditGroupActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SettingsPresenter implements SettingsContract.Presenter {

    private final SettingsContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;

    private CompositeSubscription subscriptions;

    @Inject
    public SettingsPresenter(SettingsContract.View view,
                             ActivityScreenSwitcher screenSwitcher,
                             DataService dataService) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
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
    public void start() {
        subscriptions = new CompositeSubscription();
        initToolbar();
        subscriptions.add(dataService.getCachedGroups()
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(view::bindGroups));
    }

    private void initToolbar() {
        view.setToolbarNavigationIcon(R.drawable.ic_arrow_back_white_24dp, v -> {
            goBack();
        });
    }

    public void goBack() {
        screenSwitcher.goBack();
    }

    @Override
    public void onEditGroup(long id) {
        screenSwitcher.open(new EditGroupActivity.Screen(id));
    }

    @Override
    public void onChangePasswordViewClick() {
        screenSwitcher.open(new ChangePasswordActivity.Screen());

    }

    @Override
    public void onLogOutViewClick() {
        subscriptions.add(dataService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logOuted -> {
                    if (logOuted) {
                        screenSwitcher.open(new EnterActivity.Screen());
                    }
                }));
    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }
}
