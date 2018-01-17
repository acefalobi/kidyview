package com.ltst.schoolapp.parent.ui.checkout.select.child.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.View;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.data.uimodel.SelectPersonModel.DiffCallback;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.ui.holder.SelectPersonViewHolder;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.checkout.select.child.ChecksSelectChildActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ChecksSelectChildPresenter implements ChecksSelectChildContract.Presenter,
        SelectPersonViewHolder.SelectPersonActionListener {

    public static final String KEY_CHECKED_ITEMS = "SelectPersonPresenter.checked.items";
    private final ChecksSelectChildContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final ArrayList<Long> selectedIds;
    private final ArrayList<Child> children;
    private SimpleBindableAdapter<SelectPersonModel> recyclerAdapter;
    private CompositeSubscription subscriptions;

    @Inject
    public ChecksSelectChildPresenter(ChecksSelectChildContract.View view,
                                      ActivityScreenSwitcher screenSwitcher,
                                      DataService dataService,
                                      Bundle activityParams) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
//        this.selectedIds = activityParams.getIntegerArrayList(ChecksSelectChildActivity.Screen.KEY_SELECTED_IDS);
        this.selectedIds = ((ArrayList<Long>) activityParams.getSerializable(ChecksSelectChildActivity.Screen.KEY_SELECTED_IDS));
        this.children = activityParams.getParcelableArrayList(ChecksSelectChildActivity.Screen.KEY_CHILDREN);

        recyclerAdapter = new SimpleBindableAdapter<>(
                R.layout.viewholder_select_person_item,
                SelectPersonViewHolder.class);
        recyclerAdapter.setActionListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  START SCREEN  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        view.bindAdapter(recyclerAdapter);
        List<SelectPersonModel> models = SelectPersonModel.fromChildList(children);
        for (SelectPersonModel selectPersonModel : models) {
            if (selectedIds == null || selectedIds.size() == 0) {
                break;
            }
            for (Long id : selectedIds) {
                if (selectPersonModel.getServerId() == id) {
                    selectPersonModel.setSelected(true);
                }
            }
        }
        List<SelectPersonModel> items = recyclerAdapter.getItems();
        final DiffCallback diffCallback = new DiffCallback(items, models);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items.clear();
        items.addAll(models);
        diffResult.dispatchUpdatesTo(recyclerAdapter);
    }

    @Override
    public void start() {
        if (subscriptions.isUnsubscribed()) subscriptions = new CompositeSubscription();
        initToolbar();
        view.bindListeners(onNextClick());
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
    private Action1<List<SelectPersonModel>> bindData = new Action1<List<SelectPersonModel>>() {
        @Override
        public void call(List<SelectPersonModel> models) {
            List<SelectPersonModel> items = recyclerAdapter.getItems();
            final DiffCallback diffCallback = new DiffCallback(items, models);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            items.clear();
            items.addAll(models);
            diffResult.dispatchUpdatesTo(recyclerAdapter);
        }
    };

    private ArrayList<SelectPersonModel> getCheckedItems() {
        ArrayList<SelectPersonModel> checkedList = new ArrayList<>();
        for (SelectPersonModel model : recyclerAdapter.getItems()) {
            if (model.isSelected()) checkedList.add(model);
        }
        return checkedList;
    }

    @Override
    public void OnItemClickListener(int position, SelectPersonModel item) {
        SelectPersonModel posItem = recyclerAdapter.getItem(position);
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
//        screenSwitcher.open(new ChecksSelectMemberActivity.Screen(params, getCheckedItems()));
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

    @Override
    public void closeForResult() {
        ArrayList<Long> selectedIds = new ArrayList<>();
        List<SelectPersonModel> items = recyclerAdapter.getItems();
        for (SelectPersonModel selectPersonModel : items){
            if (selectPersonModel.isSelected()){
                selectedIds.add(selectPersonModel.getServerId());
            }
        }
        Intent intent = new Intent();
        intent.putExtra(ChecksSelectChildActivity.Screen.KEY_SELECTED_IDS,selectedIds);
        screenSwitcher.setResultAndGoBack(intent);
    }
}
