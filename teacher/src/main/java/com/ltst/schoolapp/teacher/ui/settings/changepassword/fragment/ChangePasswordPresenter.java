package com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.data.request.PasswordUpdateRequest;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.net.exceptions.AuthException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.enter.EnterActivity;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ltst.schoolapp.teacher.ui.enter.EnterActivity.Screen.Params.EnterFragment.RESTORE_PASSWORD;

public class ChangePasswordPresenter implements ChangePasswordContract.Presenter {

    private final ChangePasswordContract.View view;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;

    private CompositeSubscription subscriptions;

    @Inject
    public ChangePasswordPresenter(ChangePasswordContract.View view,
                                   DataService dataService,
                                   ActivityScreenSwitcher activitySwitcher) {
        this.view = view;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
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
    }

    private void initToolbar() {
        view.setToolbarNavigationIcon(R.drawable.ic_clear_white_24dp, v -> {
            activitySwitcher.goBack();
        });
    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }

    @Override
    public void validateAndUpdate(Map<ValidateType, String> needValidate) {
        FieldsValidator.validate(needValidate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateGroup, this::showValidateError);
    }

    @Override
    public void openForgotPassword() {
        activitySwitcher.open(new EnterActivity.Screen(RESTORE_PASSWORD));
    }

    private void updateGroup(Map<ValidateType, String> validated) {
        String oldPassword = validated.get(ValidateType.OLD_PASSWORD);
        String newPassword = validated.get(ValidateType.PASSWORD);
        PasswordUpdateRequest request = new PasswordUpdateRequest(newPassword, oldPassword);
        subscriptions.add(dataService.updatePassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile -> {
                    activitySwitcher.goBack();
                }, throwable -> {
                    if (throwable instanceof AuthException) {
                        view.errorOldPassword();
                    }
                }));
    }

    private void showValidateError(Throwable throwable) {
        ValidationThrowable validatedThrowable = ((ValidationThrowable) throwable);
        Set<ValidateType> notValidatedTypes = validatedThrowable.keySet();
        if (notValidatedTypes.contains(ValidateType.CONFIRM)) {
            view.errorConfirmPassword();
        } else if (notValidatedTypes.contains(ValidateType.PASSWORD)) {
            view.errorPassword();
        } else if (notValidatedTypes.contains(ValidateType.OLD_PASSWORD)) {
            view.errorOldPassword();
        }
    }

}
