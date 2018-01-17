package com.ltst.schoolapp.teacher.ui.child.checkemail;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.child.addmember.AddMemberFragment;
import com.ltst.schoolapp.teacher.ui.child.addmember.AddMemberScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CheckEmailPresenter implements CheckEmailContract.Presenter {

    private final CheckEmailContract.View view;
    private final DataService dataService;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ActivityScreenSwitcher activitySwitcher;
    private final Child child;

    private Map<ValidateType, String> emailCheck = new HashMap<>();

    private String email;
    private CompositeSubscription subscription;

    @Inject
    public CheckEmailPresenter(CheckEmailContract.View view,
                               DataService dataService,
                               FragmentScreenSwitcher fragmentSwitcher,
                               ActivityScreenSwitcher activitySwitcher,
                               Child child) {
        this.view = view;
        this.dataService = dataService;
        this.fragmentSwitcher = fragmentSwitcher;
        this.activitySwitcher = activitySwitcher;
        this.child = child;
    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }


    @Override public void start() {
        subscription = new CompositeSubscription();
    }

    @Override public void stop() {
        subscription.unsubscribe();
        subscription = null;
    }

    @Override public void firstStart() {
        view.bindName(child.getFirstName(), child.getLastName());
    }

    @Override public void setEmail(String string) {
        this.email = string;
        emailCheck.put(ValidateType.PERSONAL_EMAIL, email);
        FieldsValidator.validate(emailCheck)
                .subscribe(validateTypeStringMap -> view.enableCheckButton(true),
                        throwable -> view.enableCheckButton(false));
    }

    @Override public void done() {
        if (parentExistForThisChild(email)) {
            view.existEmailError();
            return;
        }
        checkEmailOnServer();
    }

    private boolean parentExistForThisChild(String email) {
        List<Member> allMembers = child.getAllMembers();
        for (Member member : allMembers) {
            if (member.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void checkEmailOnServer() {
        view.startLoad();
        subscription.add(dataService.findMemberByEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(findMember, findMemberError));
    }

    private final Action1<Member> findMember = member -> {
        CheckEmailPresenter.this.view.stopLoad();
        member.setId(CheckEmailPresenter.this.dataService.getFreeFamilyMemberId());
        AddMemberFragment.Screen screen = new AddMemberFragment.Screen(member, AddMemberScope.SCREEN_MODE_EXIST);
        CheckEmailPresenter.this.fragmentSwitcher.open(screen);

    };

    private final Action1<Throwable> findMemberError = throwable -> {
        CheckEmailPresenter.this.view.stopLoad();
        if (throwable instanceof NetErrorException) {
            CheckEmailPresenter.this.view.networkError();
        } else if (throwable instanceof NotFoundException) {
            Member member = new Member(CheckEmailPresenter.this.dataService.getFreeFamilyMemberId());
            member.setEmail(email);
            AddMemberFragment.Screen screen = new AddMemberFragment.Screen(member, AddMemberScope.SCREEN_MODE_CREATE);
            CheckEmailPresenter.this.fragmentSwitcher.open(screen);
        }
    };

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }
}
