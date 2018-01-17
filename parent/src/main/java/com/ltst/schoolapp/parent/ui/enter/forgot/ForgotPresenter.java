package com.ltst.schoolapp.parent.ui.enter.forgot;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.enter.newpass.NewPassFragment;

import java.util.HashMap;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ForgotPresenter implements ForgotContract.Presenter {
    private final ForgotContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final DataService dataService;
    private final DialogProvider dialogProvider;

    private String email;
    private HashMap<ValidateType, String> validateParams;
    private CompositeSubscription screenSubscription;

    @Inject
    public ForgotPresenter(ForgotContract.View view,
                           FragmentScreenSwitcher fragmentSwitcher,
                           DataService dataService,
                           DialogProvider dialogProvider,
                           ActivityScreenSwitcher activitySwitcher) {
        this.view = view;
        this.fragmentSwitcher = fragmentSwitcher;
        this.dataService = dataService;
        this.dialogProvider = dialogProvider;
        this.activitySwitcher = activitySwitcher;
    }

    @Override
    public void start() {
        screenSubscription = new CompositeSubscription();
        validateParams = new HashMap<>();
    }

    @Override
    public void stop() {
        if (screenSubscription != null) {
            screenSubscription.unsubscribe();
            screenSubscription = null;
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
    public void done() {
        validateParams.clear();
        validateParams.put(ValidateType.PERSONAL_EMAIL, email);
        FieldsValidator.validate(validateParams)
                .subscribe(validateTypeStringMap ->
                                resetPassword(validateTypeStringMap.get(ValidateType.PERSONAL_EMAIL)),
                        throwable -> {
                            if (throwable instanceof ValidationThrowable) {
                                view.emailRegexError();
                            }
                        });
    }

    private void resetPassword(String email) {
        view.startLoad();
        screenSubscription.add(dataService.resetPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    view.stopLoad();
                    showSendWarningDialog();
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetworkErrorException) {
                        view.showNetworkError();
                    } else {
                        view.emailNotExist();
                    }
                }));
    }

    private void showSendWarningDialog() {
        dialogProvider.showSendNewCodeMessage((dialog, which)
                -> fragmentSwitcher.open(new NewPassFragment.Screen(email)));
    }


    @Override
    public void goBack() {
        activitySwitcher.goBack();
    }

    @Override
    public void setEmail(String text) {
        email = text;
    }
}
