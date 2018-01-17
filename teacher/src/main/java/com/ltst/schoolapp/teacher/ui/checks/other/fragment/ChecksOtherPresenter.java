package com.ltst.schoolapp.teacher.ui.checks.other.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextWatcher;
import android.view.View;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.util.SimpleTextWatcher;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.checks.code.ChecksCodeActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivity;
import com.ltst.schoolapp.teacher.ui.checks.single.check.SingleCheckActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChecksOtherPresenter implements ChecksOtherContract.Presenter {
    private final ChecksOtherContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final List<Long> childrenIds;
    private final boolean isCheckIn;
    private final long groupId;
    private final Bundle params;

    private String firstName;
    private String lastName;
    private CompositeSubscription subscriptions;

    @Inject
    public ChecksOtherPresenter(ChecksOtherContract.View view,
                                ActivityScreenSwitcher screenSwitcher,
                                DataService dataService,
                                Bundle params) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
        String keySelectedChildren = ChecksSelectMemberActivity.Screen.KEY_SELECTED_CHILDREN;
        List<SelectPersonModel> children = params.getParcelableArrayList(keySelectedChildren);
        this.childrenIds = SelectPersonModel.getServerIdList(children);
        String keyCheckIn = ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN;
        isCheckIn = params.getBoolean(keyCheckIn);
        String keySelectedGrooup = ChecksSelectMemberActivity.Screen.KEY_SELECTED_GROOUP;
        groupId = params.getLong(keySelectedGrooup);
        this.params = params;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  START SCREEN  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
    }

    @Override
    public void start() {
        subscriptions = new CompositeSubscription();
        initToolbar();
        view.bindListeners(getFirstNameWatcher(), getLastNameWatcher(), onNextClick());
        changeNextEnabledIfNeeded();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  TOOLBAR  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void initToolbar() {
        view.initToolbar(R.drawable.ic_arrow_back_white_24dp, v -> {
            goBack();
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////  FIRST AND LAST NAME  ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private TextWatcher getFirstNameWatcher() {
        return new SimpleTextWatcher(firstName -> {
            this.firstName = firstName.toString();
            changeNextEnabledIfNeeded();
        });
    }

    private TextWatcher getLastNameWatcher() {
        return new SimpleTextWatcher(lastName -> {
            this.lastName = lastName.toString();
            changeNextEnabledIfNeeded();
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  NEXT  ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void changeNextEnabledIfNeeded() {
        view.setNextEnabled(!StringUtils.isBlank(firstName) && !StringUtils.isBlank(lastName));
    }

    private View.OnClickListener onNextClick() {
        return onNextClick -> {
            if (isCheckIn) {
                checkIn();
            } else {
                openChecksCodeScreen();
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////  CHECKIN AND CHECKOUT  ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void checkIn() {
        subscriptions.add(dataService.checkIn(groupId, childrenIds, Member.createOther(firstName, lastName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openSingleCheckScreen, Throwable::printStackTrace));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////   NAVIGATION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void goBack() {
        screenSwitcher.goBack();
    }

    private void openSingleCheckScreen(List<ChildCheck> childChecks) {
        screenSwitcher.open(new SingleCheckActivity.Screen(new ArrayList<>(childChecks)));
    }

    private void openChecksCodeScreen() {
        screenSwitcher.open(new ChecksCodeActivity.Screen(params, groupId, firstName, lastName));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  LIFECYCLE  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }

}
