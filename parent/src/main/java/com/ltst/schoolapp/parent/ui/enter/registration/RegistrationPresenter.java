package com.ltst.schoolapp.parent.ui.enter.registration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.LoginException;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.net.exceptions.ServerDataBaseException;
import com.ltst.core.net.response.ServerDBExceptionResponse;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.Result;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RegistrationPresenter implements RegistrationContract.Presenter {

    private final RegistrationContract.View view;
    private final DataService dataService;
    private final FragmentScreenSwitcher fragmentScreenSwitcher;
    private final ActivityScreenSwitcher activitySwitcher;

    private String email = StringUtils.EMPTY;
    private String password = StringUtils.EMPTY;
    private String confirmPassword = StringUtils.EMPTY;
    private String code = StringUtils.EMPTY;

    private CompositeSubscription compositeSubscription;


    @Inject
    public RegistrationPresenter(RegistrationContract.View view,
                                 DataService dataService,
                                 FragmentScreenSwitcher fragmentScreenSwitcher,
                                 ActivityScreenSwitcher activityScreenSwitcher) {
        this.view = view;
        this.dataService = dataService;
        this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        this.activitySwitcher = activityScreenSwitcher;
    }

    @Override
    public void start() {
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void stop() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
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

    @Override
    public void login() {
        Map<ValidateType, String> loginValidationFields = new HashMap<>();
        loginValidationFields.put(ValidateType.PERSONAL_EMAIL, email);
        loginValidationFields.put(ValidateType.PASSWORD, password);
        loginValidationFields.put(ValidateType.CONFIRM, confirmPassword);
        loginValidationFields.put(ValidateType.CODE, code);
        FieldsValidator.validate(loginValidationFields)
                .subscribe(this::registration,
                        throwable -> {
                            if (throwable instanceof ValidationThrowable) {
                                ValidationThrowable validationThrowable = (ValidationThrowable) throwable;
                                Set<ValidateType> validateTypes = validationThrowable.keySet();
                                if (validateTypes.contains(ValidateType.PERSONAL_EMAIL)) {
                                    view.emailValidationError();
                                }
                                if (validateTypes.contains(ValidateType.CODE)) {
                                    view.codeValidationError();
                                }
                                if (validateTypes.contains(ValidateType.PASSWORD)) {
                                    view.passwordValidationError();
                                } else if (validateTypes.contains(ValidateType.CONFIRM)) {
                                    view.passwordConfirmError();
                                }

                            }
                        });
    }

    private void registration(Map<ValidateType, String> validateTypeStringMap) {
        view.startLoad();
        String email = validateTypeStringMap.get(ValidateType.PERSONAL_EMAIL);
        String password = validateTypeStringMap.get(ValidateType.PASSWORD);
        String code = validateTypeStringMap.get(ValidateType.CODE);
        compositeSubscription.add(dataService.registration(email, password, code)
                .flatMap(profile -> dataService.updateChildrenInGroups())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    view.stopLoad();
                    activitySwitcher.open(new MainActivity.Screen());
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof ServerDataBaseException) {
                        view.serverRegistrationError();
                    } else if (throwable instanceof NetErrorException) {
                        view.netError();
                    }
                }));
    }

    @Override public void sendCodeAgain() {
        view.startLoad();
        HashMap<ValidateType, String> resendCodeValidationFields = new HashMap<>();
        resendCodeValidationFields.put(ValidateType.PERSONAL_EMAIL, email);
        FieldsValidator.validate(resendCodeValidationFields)
                .flatMap(validateTypeStringMap ->
                        dataService.sendCodeAgain(validateTypeStringMap.get(ValidateType.PERSONAL_EMAIL)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::codeSent, this::sendCodeThrowable);
    }

    private void codeSent(Result<Void> result) {
        view.stopLoad();
        if (result.isError()) {
            serverError(result);
        } else {
            view.sendAgainSuccess();
        }
    }

    private void serverError(Result<Void> result) {
        Throwable error = result.error();
        if (error instanceof NotFoundException) {
            view.sendAgainLoggedInUserError();
        } else if (error instanceof NetErrorException) {
            view.netError();
        }
    }

    private void sendCodeThrowable(Throwable throwable) {
        view.stopLoad();
        if (throwable instanceof ValidationThrowable) {
            view.emailValidationError();
        }
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setConfirmPassword(String password) {
        this.confirmPassword = password;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }
}
