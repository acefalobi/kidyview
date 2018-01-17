package com.ltst.schoolapp.teacher.ui.enter.login;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.LoginException;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment;
import com.ltst.schoolapp.teacher.ui.enter.forgot.ForgotFragment;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;
    private final FragmentScreenSwitcher fragmentScreenSwitcher;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;

    private Profile enterProfile;

    @Inject
    public LoginPresenter(LoginContract.View view,
                          FragmentScreenSwitcher fragmentScreenSwitcher,
                          DataService dataService,
                          ActivityScreenSwitcher activitySwitcher,
                          Profile profile) {
        this.view = view;
        this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.enterProfile = profile;
    }


    @Override
    public void validate(final Map<ValidateType, String> needValidate, final String password) {
        if (StringUtils.isBlank(password)) {
            view.setEmptyPasswordError();
            return;
        }
        view.startLoad();
        FieldsValidator.validate(needValidate)
                .flatMap(validated -> dataService.login(validated.get(ValidateType.PERSONAL_EMAIL), password))
                .flatMap(profile -> {
                    if (!StringUtils.isBlank(profile.getFirstName())) {
                        return dataService.layerConnect()
                                .filter(aBoolean -> {
                                    if (aBoolean) {
                                        return true;
                                    } else return false;
                                })
                                .flatMap(aBoolean -> Observable.just(profile));
                    } else {
                        return Observable.just(profile);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile -> {
                    if (!StringUtils.isBlank(profile.getFirstName())) {
                        dataService.enableTokenExceptionHandler();
                        activitySwitcher.open(new MainActivity.Screen());
                    } else {
                        enterProfile.setEmail(profile.getEmail());
                        fragmentScreenSwitcher.openWithClearStack(new EditProfileFragment.Screen(EditProfileFragment.DEFAULT));
                    }
                }, throwable1 -> {
                    view.stopLoad();
                    if (throwable1 instanceof NetErrorException) {
                        view.showNetError();
                    } else if (throwable1 instanceof ValidationThrowable) {
                        Set<ValidateType> validateTypes = ((ValidationThrowable) throwable1)
                                .notValidatedParams.keySet();
                        if (validateTypes.contains(ValidateType.PERSONAL_EMAIL)) {
                            view.errorEmail();
                        }
                    } else if (throwable1 instanceof LoginException) {
                        view.loginError();
                    } else if (throwable1 instanceof NotFoundException) {
                        view.loginError();
                    }

                });
    }

//    private void layerConnect(Profile profile) {
//        dataService.layerConnect(profile)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(aBoolean -> {
//                    if (aBoolean) {
//                        activitySwitcher.open(new MainActivity.Screen());
//                    }
//                });
////
//    }

    @Override
    public void forgotPassword() {
        fragmentScreenSwitcher.open(new ForgotFragment.Screen());
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
