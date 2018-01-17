package com.ltst.schoolapp.teacher.ui.enter.registration;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.ServerDataBaseException;
import com.ltst.core.net.response.ServerDBExceptionResponse;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.enter.welcome.WelcomeFragment;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RegistrationPresenter implements RegistrationContract.Presenter {

    private final RegistrationContract.View view;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final DataService dataService;
    private Profile registrationProfile;

    private CompositeSubscription subscriptions;

    @Inject
    public RegistrationPresenter(RegistrationContract.View view,
                                 FragmentScreenSwitcher fragmentSwitcher,
                                 DataService dataService,
                                 Profile registrationProfile
    ) {
        this.view = view;
        this.fragmentSwitcher = fragmentSwitcher;
        this.dataService = dataService;
        this.registrationProfile = registrationProfile;
    }


    @Override
    public void start() {
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
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
                .subscribe(validated -> {
                    String email = validated.get(ValidateType.PERSONAL_EMAIL);
                    String password = validated.get(ValidateType.PASSWORD);
                    registration(email, password);
                }, throwable -> {
                    Map<ValidateType, String> notValidated = ((ValidationThrowable)
                            throwable).notValidatedParams;
                    for (ValidateType param : notValidated.keySet()) {
                        if (param.equals(ValidateType.PERSONAL_EMAIL)) {
                            view.errorRegexEmail();
                        } else if (param.equals(ValidateType.CONFIRM)) {
                            view.errorConfirmPassword();
                        } else if (param.equals(ValidateType.PASSWORD)) {
                            view.errorPassword();
                        }
                    }
                });
    }

    private void registration(final String email, String password) {
        view.showLoad();
        subscriptions.add(dataService.registration(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile -> {
                    view.showContent();
                    registrationProfile.setEmail(profile.getEmail());
                    fragmentSwitcher.open(new WelcomeFragment.Screen());
                }, throwable -> {
                    view.showContent();
                    if (throwable instanceof NetErrorException) {
                        view.networkError();
                    } else if (throwable instanceof ServerDataBaseException) {
                        List<String> emailList
                                = ((ServerDataBaseException) throwable).response.details.usersTeacher.emails;
                        if (email != null && !email.isEmpty()) {
                            if (!emailList.isEmpty()) {
                                view.errorExistEmail();
                            }
                        }
                    }
                }));
    }


}
