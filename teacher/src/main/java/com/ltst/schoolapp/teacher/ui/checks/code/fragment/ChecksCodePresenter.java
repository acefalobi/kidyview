package com.ltst.schoolapp.teacher.ui.checks.code.fragment;

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

public class ChecksCodePresenter implements ChecksCodeContract.Presenter {
    public static final String KEY_CHECKED_ITEMS = "SelectPersonPresenter.checked.items";

    private final ChecksCodeContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final List<Long> childrenIds;
    private final boolean isCheckIn;
    private final String firstName;
    private final String lastName;
    private final long selectedGroupId;

    private CompositeSubscription subscriptions;
    private String code;

    @Inject
    public ChecksCodePresenter(ChecksCodeContract.View view,
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
        String keyFirstName = ChecksCodeActivity.Screen.KEY_FIRST_NAME;
        firstName = params.getString(keyFirstName);
        String keyLastName = ChecksCodeActivity.Screen.KEY_LAST_NAME;
        lastName = params.getString(keyLastName);
        String keySelectedGrooup = ChecksSelectMemberActivity.Screen.KEY_SELECTED_GROOUP;
        selectedGroupId = params.getLong(keySelectedGrooup);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  START SCREEN  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void start() {
        if (subscriptions.isUnsubscribed()) subscriptions = new CompositeSubscription();
        initToolbar();
        view.bindListeners(getCodeWatcher(), onDoneClick());
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
    ////////////////////////////////////////  CODE  ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private TextWatcher getCodeWatcher() {
        return new SimpleTextWatcher(code -> {
            this.code = code.toString();
            changeNextEnabledIfNeeded();
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  DONE  ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void changeNextEnabledIfNeeded() {
        view.setDoneEnabled(!StringUtils.isBlank(code));
    }

    private View.OnClickListener onDoneClick() {
        return onDoneClick -> {
                checkOut();
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////  CHECKIN AND CHECKOUT  ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void checkOut() {
        subscriptions.add(dataService.checkOut(selectedGroupId, childrenIds,
                Member.createOther(firstName, lastName), code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openSingleCheckScreen, throwable -> {
                    view.showCodeError();
                    throwable.printStackTrace();
                }));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////   NAVIGATION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void goBack() {
        screenSwitcher.goBack();
    }

    private void openSingleCheckScreen(List<ChildCheck> childChecks) {
        screenSwitcher.open(new SingleCheckActivity.Screen(new ArrayList<>(childChecks)));
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
