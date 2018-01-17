package com.ltst.schoolapp.parent.ui.family.add.check;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.family.add.AddMemberActivity;
import com.ltst.schoolapp.parent.ui.family.add.request.RequestFragment;
import com.ltst.schoolapp.parent.ui.family.add.request.RequestPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CheckEmailPresenter implements CheckEmailContract.Presenter {

    private final CheckEmailContract.View view;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private Member member;
    private String email;
    private final String name;
    private final String lastName;
    private final ArrayList<String> existParentsEmails;

    private CompositeSubscription subscription;

    @Inject
    public CheckEmailPresenter(CheckEmailContract.View view,
                               DataService dataService,
                               ActivityScreenSwitcher activitySwitcher,
                               FragmentScreenSwitcher fragmentSwitcher,
                               Member member,
                               Bundle screenParams) {
        this.view = view;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.fragmentSwitcher = fragmentSwitcher;
        this.member = member;
        this.name = screenParams.getString(AddMemberActivity.Screen.CHILD_FIRST_NAME_KEY);
        this.lastName = screenParams.getString(AddMemberActivity.Screen.CHILD_LAST_NAME_KEY);
        this.existParentsEmails = screenParams.getStringArrayList(AddMemberActivity.Screen.CHILD_EXISTING_PARENTS);
    }

    @Override public void start() {
        subscription = new CompositeSubscription();
    }

    @Override public void stop() {
        subscription.unsubscribe();
        subscription = null;
    }

    @Override public void firstStart() {
        view.bindName(name, lastName);
    }

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }

    private final Map<ValidateType, String> validateEmail = new HashMap<>();

    @Override public void setEmail(String string) {
        this.email = string;
        validateEmail.put(ValidateType.PERSONAL_EMAIL, string);
        FieldsValidator.validate(validateEmail)
                .subscribe(validateTypeStringMap -> {
                    view.enableCheckButton(true);
                }, throwable -> {
                    view.enableCheckButton(false);
                });
    }

    @Override public void done() {
        for (String existParentsEmail : existParentsEmails) {
            if (existParentsEmail.equals(email)) {
                view.existEmailError();
                return;
            }
        }
        view.startLoad();
        subscription.add(dataService.findByEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(findMemberSubscribe, requestThrowable));
    }

    private final Action1<Member> findMemberSubscribe = member1 -> {
        CheckEmailPresenter.this.view.stopLoad();
        CheckEmailPresenter.this.member.setFirstName(member1.getFirstName());
        CheckEmailPresenter.this.member.setLastName(member1.getLastName());
        CheckEmailPresenter.this.member.setAvatarUrl(member1.getAvatarUrl());
        CheckEmailPresenter.this.member.setEmail(member1.getEmail());
        CheckEmailPresenter.this.member.setPhone(member1.getPhone());
        CheckEmailPresenter.this.member.setSecondPhone(member1.getSecondPhone());
        nextScreen(RequestPresenter.MEMBER_EXIST);
    };

    private final Action1<Throwable> requestThrowable = throwable -> {
        CheckEmailPresenter.this.view.stopLoad();
        if (throwable instanceof NetErrorException) {
            CheckEmailPresenter.this.view.networkError();
        } else {
            if (throwable instanceof NotFoundException) {
                CheckEmailPresenter.this.member.setFirstName(StringUtils.EMPTY);
                CheckEmailPresenter.this.member.setLastName(StringUtils.EMPTY);
                CheckEmailPresenter.this.member.setAvatarUrl(StringUtils.EMPTY);
                CheckEmailPresenter.this.member.setPhone(StringUtils.EMPTY);
                CheckEmailPresenter.this.member.setSecondPhone(StringUtils.EMPTY);
                CheckEmailPresenter.this.member.setEmail(email);
                nextScreen(RequestPresenter.NEW_MEMBER);
            }
        }
    };

    private void nextScreen(@RequestPresenter.ScreenStatus int screenStatus) {
        fragmentSwitcher.open(new RequestFragment.Screen(screenStatus));
    }
}
