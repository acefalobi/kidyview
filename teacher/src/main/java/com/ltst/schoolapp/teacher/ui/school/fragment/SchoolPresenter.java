package com.ltst.schoolapp.teacher.ui.school.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.layer.atlas.util.Log;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.model.School;
import com.ltst.core.data.preferences.qualifiers.IsAdmin;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.editprofile.EditProfileActivity;
import com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SchoolPresenter implements SchoolContract.Presenter {

    private final SchoolContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final ApplicationSwitcher appSwitcher;
    private final boolean isAdmin;
    private final DataService dataService;

    private Profile profile;
    private CompositeSubscription subscriptions;

    @Inject
    public SchoolPresenter(SchoolContract.View view,
                           Profile profile,
                           ActivityScreenSwitcher activitySwitcher,
                           @IsAdmin BooleanPreference isAdmin,
                           ApplicationSwitcher appSwitcher,
                           DataService dataService) {
        this.view = view;
        this.profile = profile;
        this.activitySwitcher = activitySwitcher;
        this.isAdmin = isAdmin.get();
        this.appSwitcher = appSwitcher;
        this.dataService = dataService;
    }

    @Override public void start() {
        subscriptions = new CompositeSubscription();
    }

    @Override public void stop() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
            subscriptions = null;
        }
    }

    @Override public void firstStart() {
        School school = profile.getSchool();
        view.bindSchool(school, isAdmin);
    }

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }

    public static final int EDIT_SCHOOL_REQUEST_CODE = 5678;

    @Override public void openEditSchool() {
        activitySwitcher.startForResult(new EditProfileActivity.Screen(profile, EditProfileFragment.SCHOOL),
                EDIT_SCHOOL_REQUEST_CODE);
    }

    @Override public void goBack() {
        activitySwitcher.goBack();
        Log.d("SCHOOL BACK PRESSED");
    }

    @Override public void callPhone() {
        appSwitcher.openDial(profile.getSchool().getPhone());
    }

    @Override public void callAdditionalPhone() {
        String additionalPhone = profile.getSchool().getAdditionalPhone();
        if (!StringUtils.isBlank(additionalPhone)) {
            appSwitcher.openDial(profile.getSchool().getAdditionalPhone());
        }

    }

    @Override public void writeEmail() {
        appSwitcher.openEmailApplication(profile.getSchool().getEmail());
    }

    @Override public void afterEditSchool() {
        if (subscriptions == null) {
            subscriptions = new CompositeSubscription();
        }
        subscriptions.add(dataService.getProfileFromDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile1 -> {
                    this.profile = profile1;
                    view.bindSchool(profile1.getSchool(), isAdmin);
                }));
    }


}
