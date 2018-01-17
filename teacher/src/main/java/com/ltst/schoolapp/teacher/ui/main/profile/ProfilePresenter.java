package com.ltst.schoolapp.teacher.ui.main.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.editprofile.EditProfileActivity;
import com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment;
import com.ltst.schoolapp.teacher.ui.events.calendar.CalendarActivity;
import com.ltst.schoolapp.teacher.ui.main.ChangeGroupHelper;
import com.ltst.schoolapp.teacher.ui.school.SchoolActivity;
import com.ltst.schoolapp.teacher.ui.settings.settings.SettingsActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter implements ProfileContract.Presenter {

    private final ProfileContract.View view;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final ApplicationSwitcher applicationSwitcher;
    private final FragmentScreenSwitcher fragmentScreenSwitcher;
    private final ChangeGroupHelper spinnerHelper;

    private CompositeSubscription compositeSubscription;

    private Profile profile;

    @Inject
    public ProfilePresenter(ProfileContract.View view,
                            DataService dataService,
                            ActivityScreenSwitcher activitySwitcher,
                            ApplicationSwitcher applicationSwitcher,
                            FragmentScreenSwitcher fragmentScreenSwitcher,
                            ChangeGroupHelper spinnerHelper) {
        this.view = view;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.applicationSwitcher = applicationSwitcher;
        this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        this.spinnerHelper = spinnerHelper;
    }

    @Override
    public void start() {
        spinnerHelper.showSpinner(false);
        compositeSubscription = new CompositeSubscription();
        loadProfile();
    }

    private void loadProfile() {
        compositeSubscription.add(dataService.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(obtainProfile, Throwable::printStackTrace));
    }

    private Action1<Profile> obtainProfile = new Action1<Profile>() {
        @Override
        public void call(Profile profile) {
            ProfilePresenter.this.profile = profile;
            view.bindData(profile);
        }
    };


    @Override
    public void sendEmail(String email) {
        applicationSwitcher.openEmailApplication(email);
    }

    @Override
    public void openAvatar() {
        String avatarUrl = profile.getAvatarUrl();
        if (!StringUtils.isBlank(avatarUrl)) {
            fragmentScreenSwitcher.showDialogFragment(new SimpleImageFragment.Screen(avatarUrl));
        }
    }

    @Override public void openEvents() {
        activitySwitcher.open(new CalendarActivity.Screen());
    }

    @Override public void openSchoolInfo() {
        activitySwitcher.open(new SchoolActivity.Screen(profile));
    }

    @Override
    public void goToEditProfile() {
        activitySwitcher.open(new EditProfileActivity.Screen(profile, EditProfileFragment.PROFILE));
    }

    @Override
    public void goToSettings() {
        activitySwitcher.open(new SettingsActivity.Screen());
    }

    @Override
    public void stop() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
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
