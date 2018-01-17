package com.ltst.schoolapp.teacher.ui.checks.select.child.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.View;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.data.uimodel.SelectPersonModel.DiffCallback;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.ui.holder.SelectPersonViewHolder;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.addchild.fragment.SelectableGroup;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChecksSelectChildPresenter implements ChecksSelectChildContract.Presenter,
        SelectPersonViewHolder.SelectPersonActionListener {
    public static final String KEY_CHECKED_ITEMS = "SelectPersonPresenter.checked.items";

    private final ChecksSelectChildContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final Bundle params;

    private SimpleBindableAdapter<SelectPersonModel> childrenAdapter;
    private SimpleBindableAdapter<SelectableGroup> groupsAdapter =
            new SimpleBindableAdapter<>(R.layout.viewholder_choose_child_group_item, ChooseGroupHolder.class);

    private long selectedGroupId = 0;
    private Group tempGroup;


    private CompositeSubscription subscriptions;

    @Inject
    public ChecksSelectChildPresenter(ChecksSelectChildContract.View view,
                                      ActivityScreenSwitcher screenSwitcher,
                                      DataService dataService,
                                      Bundle activityParams) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
        this.params = activityParams;

        childrenAdapter = new SimpleBindableAdapter<>(
                R.layout.viewholder_select_person_item,
                SelectPersonViewHolder.class);
        childrenAdapter.setActionListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  START SCREEN  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        view.bindAdapter(childrenAdapter);
        subscriptions.add(dataService.getSelectedGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(firstGroupInit));
    }

    private Action1<Group> firstGroupInit = group -> {
        ChecksSelectChildPresenter.this.tempGroup = group;
        getChildrenForScreen(tempGroup.getId());
        selectedGroupId = tempGroup.getId();
    };

    @Override
    public void start() {
        if (subscriptions.isUnsubscribed()) subscriptions = new CompositeSubscription();
        initToolbar();
        view.bindListeners(onNextClick());
    }

    private void getChildrenForScreen(long groupId) {
        childrenAdapter.clear();
        subscriptions.add(dataService.getSelectChildDataFromDb(groupId, false)
                .flatMap(selectPersonModels -> {
                    if (selectPersonModels.size() == 0) {
                        dataService.changeSelectedGroup(groupId);
                        view.startLoad();
                        return dataService.getChildrenFromServer(groupId)
                                .map(SelectPersonModel::fromChildList);
                    } else return Observable.just(selectPersonModels);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bindData, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.networkError();
                    }
                }));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  TOOLBAR  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void initToolbar() {
        view.initToolbar(R.drawable.ic_arrow_back_white_24dp, onNavIconClick -> goBack());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  NEXT  ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener onNextClick() {
        return onNextClick -> openSelectMemberScreen();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////  ITEMS  ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////


    private Action1<List<SelectPersonModel>> bindData = models -> {
        ChecksSelectChildPresenter.this.view.stopLoad();
        List<SelectPersonModel> items = childrenAdapter.getItems();
        final DiffCallback diffCallback = new DiffCallback(items, models);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items.clear();
        items.addAll(models);
        diffResult.dispatchUpdatesTo(childrenAdapter);
    };

    private ArrayList<SelectPersonModel> getCheckedItems() {
        ArrayList<SelectPersonModel> checkedList = new ArrayList<>();
        for (SelectPersonModel model : childrenAdapter.getItems()) {
            if (model.isSelected()) checkedList.add(model);
        }
        return checkedList;
    }

    @Override
    public void OnItemClickListener(int position, SelectPersonModel item) {
        SelectPersonModel posItem = childrenAdapter.getItem(position);
        posItem.setSelected(!posItem.isSelected());
        view.setNextButtonEnabled(getCheckedItems().size() > 0);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////   NAVIGATION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void goBack() {
        screenSwitcher.goBack();
    }

    private void openSelectMemberScreen() {
        screenSwitcher.open(new ChecksSelectMemberActivity.Screen(params, selectedGroupId, getCheckedItems()));
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
