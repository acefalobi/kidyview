package com.ltst.schoolapp.parent.ui.enter.login;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.enter.forgot.ForgotFragment;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final DataService dataService;

    private String email = StringUtils.EMPTY;
    private String password = StringUtils.EMPTY;

    private CompositeSubscription subscription;

    @Inject
    public LoginPresenter(LoginContract.View view,
                          ActivityScreenSwitcher activitySwitcher,
                          FragmentScreenSwitcher fragmentSwitcher,
                          DataService dataService) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.fragmentSwitcher = fragmentSwitcher;
        this.dataService = dataService;
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

    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void done() {
        Map<ValidateType, String> needValidate = new HashMap<>();
        needValidate.put(ValidateType.PERSONAL_EMAIL, email);
        FieldsValidator.validate(needValidate)
                .subscribe(validateTypeStringMap -> {
                    if (StringUtils.isBlank(password)) {
                        view.emptyPassword();
                    } else {
                        loginOnServer(validateTypeStringMap);
                    }
                }, throwable -> {
                    view.emailValidationError();
                });

    }

    private void loginOnServer(Map<ValidateType, String> validateTypeStringMap) {
        view.startLoad();
        subscription.add(dataService.login(email, password)
                .flatMap(profile -> dataService.updateChildrenInGroups())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    view.stopLoad();
                    activitySwitcher.open(new MainActivity.Screen());
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.netError();
                    } else {
                        view.serverLoginError();
                    }
                }));
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override public void forgotPassword() {
        fragmentSwitcher.open(new ForgotFragment.Screen());
    }
}
