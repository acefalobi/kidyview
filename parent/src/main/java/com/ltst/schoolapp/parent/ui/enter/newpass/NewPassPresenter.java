package com.ltst.schoolapp.parent.ui.enter.newpass;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.request.RecoveryPasswordRequest;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NewPassPresenter implements NewPassContract.Presenter {

    private final NewPassContract.View view;
    private final Bundle screenParams;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final DataService dataService;
    private final Profile enterProfile;
    private final ActivityScreenSwitcher activitySwitcher;

    private RecoveryPasswordRequest request;
    private Map<ValidateType, String> screenData = new HashMap<>();
    private CompositeSubscription subscription;

    @Inject
    public NewPassPresenter(NewPassContract.View view,
                            Bundle screenParams,
                            FragmentScreenSwitcher fragmentSwitcher,
                            DataService dataService,
                            Profile enterProfile,
                            ActivityScreenSwitcher activitySwitcher) {
        this.view = view;
        this.screenParams = screenParams;
        this.fragmentSwitcher = fragmentSwitcher;
        this.dataService = dataService;
        this.enterProfile = enterProfile;
        this.activitySwitcher = activitySwitcher;
    }

    @Override
    public void start() {
        subscription = new CompositeSubscription();
    }

    @Override
    public void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }

    }

    @Override
    public void firstStart() {
        String restoreEmail = screenParams.getString(NewPassFragment.Screen.RESTORE_PASS_EMAIL);
        request = new RecoveryPasswordRequest(restoreEmail, StringUtils.EMPTY, StringUtils.EMPTY, null);

        view.setEmail(restoreEmail);
    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void goBack() {
        fragmentSwitcher.goBack();
    }

    @Override
    public void setCode(String code) {
        request.setCode(code);
    }

    @Override
    public void setPassword(String password) {
        request.setPassword(password);
    }

    @Override
    public void checkData() {
        screenData.clear();
        screenData.put(ValidateType.PERSONAL_EMAIL, request.getEmail());
        screenData.put(ValidateType.PASSWORD, request.getPassword());
        FieldsValidator.validate(screenData)
                .subscribe(validateTypeStringMap -> {
                    recoveryPassword();
                }, throwable -> {
                    if (throwable instanceof ValidationThrowable) {
                        Map<ValidateType, String> notValidated =
                                ((ValidationThrowable) throwable).notValidatedParams;
                        for (ValidateType validateType : notValidated.keySet()) {
                            if (validateType.equals(ValidateType.PASSWORD)) {
                                view.validatePasswordError();
                            }
                        }
                    }
                });
    }

    private void recoveryPassword() {
        view.startLoad();
        subscription.add(dataService.recoveryPassword(request)
                .flatMap(profile -> {
                    return dataService.updateChildrenInGroups()
                            .flatMap(childInGroups -> dataService.layerConnect())
                            .filter(aBoolean -> aBoolean)
                            .flatMap(aBoolean -> {
                                return Observable.just(profile);
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile -> {
                    view.stopLoad();
                    if (StringUtils.isBlank(profile.getFirstName())) {
                        enterProfile.setEmail(profile.getEmail());
                        activitySwitcher.open(new MainActivity.Screen());
                    } else {
                        activitySwitcher.open(new MainActivity.Screen());
                    }
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetworkErrorException) {
                        view.showNetworkError();
                    } else if (throwable instanceof NotFoundException) {
                        view.wrongNumberError();
                    } else {
                    }
                }));
        ;
    }
}
