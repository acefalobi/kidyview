package com.ltst.schoolapp.teacher.ui.enter.code;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.net.exceptions.ServerDataBaseException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CodePresenter implements CodeContract.Presenter {

    private final CodeContract.View view;
    //    private final FragmentScreenSwitcher fragmentSwitcher;
    private final DataService dataService;
    private final Profile registrationProfile;
    private final ActivityScreenSwitcher activityScreenSwitcher;
    private CompositeSubscription compositeSubscription;

    @Inject
    public CodePresenter(CodeContract.View view,
                         FragmentScreenSwitcher fragmentSwitcher,
                         DataService dataService,
                         Profile registrationProfile,
                         ActivityScreenSwitcher activityScreenSwitcher) {

        this.view = view;
        this.dataService = dataService;
        this.registrationProfile = registrationProfile;
        this.activityScreenSwitcher = activityScreenSwitcher;
    }

    @Override
    public void start() {

        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void stop() {

        compositeSubscription.unsubscribe();
        compositeSubscription = null;
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
    public void validate(Map<ValidateType, String> needValidate) {

        FieldsValidator.validate(needValidate)
                .subscribe(new Action1<Map<ValidateType, String>>() {
                    @Override
                    public void call(Map<ValidateType, String> validated) {

                        registration(validated);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        Map<ValidateType, String> notValidatedParams =
                                ((ValidationThrowable) throwable).notValidatedParams;
                        for (ValidateType type : notValidatedParams.keySet()) {
                            if (type.equals(ValidateType.PERSONAL_EMAIL)) {
                                view.emailValidateError();
                            } else if (type.equals(ValidateType.PASSWORD)) {
                                view.passwordError();
                            } else if (type.equals(ValidateType.CONFIRM)) {
                                view.confirmPasswordError();
                            } else if (type.equals(ValidateType.CODE)) {
                                view.codeError();
                            }
                        }
                    }
                });
    }

    private void registration(Map<ValidateType, String> validated) {

        view.startLoad();
        String email = validated.get(ValidateType.PERSONAL_EMAIL);
        String password = validated.get(ValidateType.PASSWORD);
        String code = validated.get(ValidateType.CODE);
        compositeSubscription.add(dataService.registrationByInvite(email, password, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Profile>() {
                    @Override
                    public void call(Profile profile) {
                        view.stopLoad();
                        CodePresenter.this.registrationProfile.setEmail(profile.getEmail());
                        CodePresenter.this.registrationProfile.setFirstName(profile.getFirstName());
                        CodePresenter.this.registrationProfile.setLastName(profile.getLastName());
                        CodePresenter.this.registrationProfile.setPhone(profile.getPhone());
                        CodePresenter.this.registrationProfile.setAdditionalPhone(profile.getAdditionalPhone());
                        CodePresenter.this.registrationProfile.setSchool(profile.getSchool());
                        activityScreenSwitcher.open(new MainActivity.Screen(BottomScreen.PROFILE));

                    }
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.showNetworkError();
                    } else if (throwable instanceof ServerDataBaseException) {
                        view.codeError();
                    } else if (throwable instanceof NotFoundException) {
                        view.emailServerInvalidError();
                    }
                }));

    }


}
