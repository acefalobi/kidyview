package com.ltst.schoolapp.teacher.ui.checks.single.check.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;

import java.util.List;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

import static com.ltst.schoolapp.teacher.ui.checks.single.check.SingleCheckActivity.Screen.KEY_CHILDREN_CHECKS;

public class SingleCheckPresenter implements SingleCheckContract.Presenter {
    private final SingleCheckContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final List<ChildCheck> childrenChecks;

    private SimpleBindableAdapter<ChildCheck> recyclerAdapter;

    private CompositeSubscription subscriptions;

    @Inject
    public SingleCheckPresenter(SingleCheckContract.View view,
                                ActivityScreenSwitcher screenSwitcher,
                                DataService dataService,
                                Bundle activityParams) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
        this.childrenChecks = activityParams.getParcelableArrayList(KEY_CHILDREN_CHECKS);

        recyclerAdapter = new SimpleBindableAdapter<>(
                R.layout.fragment_single_check_item,
                SingleCheckViewHolder.class);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  START SCREEN  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        view.bindAdapter(recyclerAdapter);
        recyclerAdapter.addAll(childrenChecks);
        view.scrollToBottom();
        view.addHeader(recyclerAdapter);
    }

    @Override
    public void start() {
        if (subscriptions.isUnsubscribed()) subscriptions = new CompositeSubscription();

        initToolbar();
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
    ////////////////////////////////////   NAVIGATION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void goBack() {
        screenSwitcher.goBack();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  LIFECYCLE  /////////////////////////////////////////////
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
