package com.ltst.schoolapp.teacher.ui.enter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.enter.forgot.ForgotFragment;
import com.ltst.schoolapp.teacher.ui.enter.start.StartFragment;
import com.ltst.schoolapp.teacher.ui.enter.welcome.WelcomeFragment;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class EnterPresenter implements BasePresenter {
    private final DataService dataService;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final Profile enterProfile;
    private final ActivityScreenSwitcher activitySwitcher;
    private final EnterActivity.Screen.Params screenParams;

    private CompositeSubscription subscriptions;


    @Inject
    public EnterPresenter(DataService dataService,
                          FragmentScreenSwitcher fragmentScreenSwitcher,
                          Profile enterProfile,
                          ActivityScreenSwitcher screenSwitcher,
                          EnterActivity.Screen.Params screenParams) {
        this.dataService = dataService;
        this.fragmentSwitcher = fragmentScreenSwitcher;
        this.enterProfile = enterProfile;
        this.activitySwitcher = screenSwitcher;
        this.screenParams = screenParams;
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
        openFragmentForParams();
    }

    private void openFragmentForParams() {
        subscriptions.add(dataService.onceOnlyDeauthenticateFromLayer()
                .subscribe(aBoolean -> {
                    if (fragmentSwitcher == null) return;
                    if (fragmentSwitcher.hasFragments()) return;
                    switch (screenParams.getFragment()) {
                        case RESTORE_PASSWORD:
                            fragmentSwitcher.open(new ForgotFragment.Screen());
                            break;
                        default:
                        case NONE:
                            checkTokenAndProfile();
                            break;
                    }
                }));

    }

    private void checkTokenAndProfile() {
        subscriptions.add(dataService.getServerToken()
                .flatMap(serverToken -> !StringUtils.isBlank(serverToken)
                        ? dataService.getProfileFromDb()
                        : Observable.just(null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openNextScreen, Throwable::printStackTrace));
    }

    private void openNextScreen(Profile profile) {
        if (profile != null && !StringUtils.isBlank(profile.getEmail())) {
            String email = profile.getEmail();
            enterProfile.setEmail(email);
            if (StringUtils.isBlank(profile.getFirstName())) {
                fragmentSwitcher.open(new WelcomeFragment.Screen());
            } else {
                activitySwitcher.open(new MainActivity.Screen());
                activitySwitcher.overridePendingTransition(0, 0);
            }
        } else {
            fragmentSwitcher.open(new StartFragment.Screen());
        }
    }

    @Override
    public void stop() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }
}
