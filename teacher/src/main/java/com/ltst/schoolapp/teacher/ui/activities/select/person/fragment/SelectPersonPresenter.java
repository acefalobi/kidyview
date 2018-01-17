package com.ltst.schoolapp.teacher.ui.activities.select.person.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.support.v7.util.DiffUtil;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.data.uimodel.SelectPersonModel.DiffCallback;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.ui.holder.SelectPersonViewHolder;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ltst.schoolapp.teacher.ui.activities.select.person.SelectPersonActivity.Screen.KEY_SELECTED_GROUP;
import static com.ltst.schoolapp.teacher.ui.activities.select.person.SelectPersonActivity.Screen.KEY_SELECTED_PERSONS;

public class SelectPersonPresenter implements SelectPersonContract.Presenter,
        SelectPersonViewHolder.SelectPersonActionListener {
    public static final String KEY_CHECKED_ITEMS = "SelectPersonPresenter.checked.items";

    private final SelectPersonContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final List<SelectPersonModel> oldChecked;
    private final LongSparseArray<SelectPersonModel> checked;
    private final long groupId;

    private SimpleBindableAdapter<SelectPersonModel> recyclerAdapter;

    private CompositeSubscription subscriptions;

    @Inject
    public SelectPersonPresenter(SelectPersonContract.View view,
                                 ActivityScreenSwitcher screenSwitcher,
                                 DataService dataService,
                                 Bundle activityParams) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
        this.oldChecked = activityParams.getParcelableArrayList(KEY_SELECTED_PERSONS);
        this.checked = new LongSparseArray<>();
        if (oldChecked != null && oldChecked.size() > 0) {
            for (SelectPersonModel selectPersonModel : oldChecked) {
                checked.put(selectPersonModel.getServerId(), selectPersonModel);
            }
        }
        this.groupId = activityParams.getLong(KEY_SELECTED_GROUP);

        recyclerAdapter = new SimpleBindableAdapter<>(
                R.layout.viewholder_select_person_item,
                SelectPersonViewHolder.class);
        recyclerAdapter.setActionListener(this);
    }

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        view.bindAdapter(recyclerAdapter);
        subscriptions.add(dataService.getSelectChildData(groupId, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bindData, Throwable::printStackTrace));
    }

    private Action1<List<SelectPersonModel>> bindData = models -> {
        List<SelectPersonModel> items = recyclerAdapter.getItems();
        final DiffCallback diffCallback = new DiffCallback(items, models);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        checkIfWasChecked(models);
        items.clear();
        items.addAll(models);
        diffResult.dispatchUpdatesTo(recyclerAdapter);
    };

    private void checkIfWasChecked(List<SelectPersonModel> newModels) {
        if (oldChecked == null) return;
        for (SelectPersonModel old : oldChecked) {
            if (newModels.contains(old)) {
                newModels.get(newModels.indexOf(old)).setSelected(true);
            }
        }
    }


    @Override
    public void start() {
        if (subscriptions.isUnsubscribed()) subscriptions = new CompositeSubscription();
        initToolbar();
    }

    private void initToolbar() {
        view.initToolbar(R.drawable.ic_clear_white_24dp, v -> {
                    goBack();
                },
                item -> {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(KEY_CHECKED_ITEMS, getCheckedItems());
                    screenSwitcher.setResultAndGoBack(intent);
                    return false;
                });
    }

    private ArrayList<SelectPersonModel> getCheckedItems() {
        ArrayList<SelectPersonModel> checkedList = new ArrayList<>();
        int checkedSize = checked.size();
        for (int x = 0; x < checkedSize; x++) {
            checkedList.add(checked.valueAt(x));
        }
        return checkedList;
    }

    public void goBack() {
        screenSwitcher.goBack();
    }

    @Override
    public void OnItemClickListener(int position, SelectPersonModel item) {
        SelectPersonModel adapterItem = recyclerAdapter.getItem(position);
        adapterItem.setSelected(!adapterItem.isSelected());
        recyclerAdapter.notifyItemChanged(position, item);
        if (item.getServerId() == SelectPersonModel.GROUP_ID) {
            deselectAllExceptGroup();
        } else {
            workOnItem(adapterItem);
        }
    }


    private void deselectAllExceptGroup() {
        checked.clear();
        SelectPersonModel group = recyclerAdapter.getItem(SelectPersonModel.GROUP_POSITION);
        checked.put(group.getServerId(), group);
        for (int i = 1; i < recyclerAdapter.getRealItemCount(); i++) {
            SelectPersonModel item = recyclerAdapter.getItem(i);
            if (!item.isSelected()) {
                continue;
            }
            item.setSelected(false);
            recyclerAdapter.notifyItemChanged(i);
        }
    }

    private void workOnItem(SelectPersonModel adapterItem) {
        recyclerAdapter.getItem(SelectPersonModel.GROUP_POSITION).setSelected(false);
        recyclerAdapter.notifyItemChanged(SelectPersonModel.GROUP_POSITION);
        checked.remove(SelectPersonModel.GROUP_ID);
        if (adapterItem.isSelected()) {
            checked.put(adapterItem.getServerId(), adapterItem);
        } else {
            checked.remove(adapterItem.getServerId());
        }
    }

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
