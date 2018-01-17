package com.ltst.schoolapp.parent.ui.select.dialog.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Filter;

import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.ui.adapter.dialog.DialogItem;
import com.ltst.core.ui.adapter.dialog.DialogMemberAdapter;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.conversation.ConversationActivity;
import com.ltst.schoolapp.parent.ui.select.dialog.SelectDialogMemberActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SelectMemberPresenter implements SelectMemberContract.Presenter, BindableViewHolder.ActionListener {

    private final ActivityScreenSwitcher activitySwitcher;
    private final SelectMemberContract.View view;
    private final DataService dataService;
    private final DialogMemberAdapter memberAdapter;
    private final @SelectDialogMemberActivity.ScreenMode int screenMode;
    private ArrayList<String> selectedLayerIdentitis = new ArrayList<>();
    private String filterQuery;
    private CompositeSubscription subscription;


    @Inject
    public SelectMemberPresenter(ActivityScreenSwitcher activitySwitcher,
                                 SelectMemberContract.View view,
                                 DataService dataService, int screenMode) {
        this.activitySwitcher = activitySwitcher;
        this.view = view;
        this.dataService = dataService;
        this.screenMode = screenMode;
        this.memberAdapter = new DialogMemberAdapter(this, screenMode != SelectDialogMemberActivity.START_SINGLE_CHAT);
    }

    @Override public void start() {
        view.setAdapter(memberAdapter);
        if (subscription.isUnsubscribed() && memberAdapter.getItemCount() == 0) {
            subscription = new CompositeSubscription();
            getAllTeachers();
        }
        if (filterQuery != null) {
            memberAdapter.getFilter().filter(filterQuery);
        }
    }

    @Override public void stop() {
        subscription.unsubscribe();
    }

    @Override public void firstStart() {
        if (screenMode == SelectDialogMemberActivity.START_GROUP_CHAT) {
            view.addDoneMenuItem();
        }
        subscription = new CompositeSubscription();
        getAllTeachers();
    }


    private void getAllTeachers() {
        subscription.add(dataService.getDialogItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getChildrenSubscription));
    }

    private Action1<List<DialogItem>> getChildrenSubscription = dialogItems -> {
        DialogMemberAdapter adapter = SelectMemberPresenter.this.memberAdapter;
        adapter.getItems().clear();
        adapter.getItems().addAll(dialogItems);
        Filter filter = adapter.getFilter();
        filter.filter(filterQuery == null ? StringUtils.EMPTY : filterQuery);
    };


    @Override public void OnItemClickListener(int position, Object item) {
        DialogItem dialogItem = (DialogItem) item;
        if (dialogItem.isFakeMember()) {
            activitySwitcher.open(new SelectDialogMemberActivity.Screen(SelectDialogMemberActivity.START_GROUP_CHAT));
        } else {
            if (screenMode == SelectDialogMemberActivity.START_SINGLE_CHAT) {
                startSingleChat(dialogItem);
            } else {
                workWithItemInList(position, dialogItem);
            }

        }
    }

    private void startSingleChat(DialogItem dialogItem) {
        String[] layerIds = {dialogItem.getLayerIdentity()};
        String screenTitle = dialogItem.getFirstName() + StringUtils.SPACE + dialogItem.getLastName();
        activitySwitcher.open(new ConversationActivity.Screen(layerIds, screenTitle));
    }

    private void workWithItemInList(int position, DialogItem dialogItem) {
        boolean checked = dialogItem.isChecked();
        String layerIdentity = dialogItem.getLayerIdentity();
        if (checked) {
            int indexOfLayerIdentity = findIndexOfLayerIdentity(layerIdentity);
            if (indexOfLayerIdentity >= 0) {
                selectedLayerIdentitis.remove(indexOfLayerIdentity);
            }
        } else {
            selectedLayerIdentitis.add(layerIdentity);
        }
        memberAdapter.getItems().get(position).setChecked(!checked);
        memberAdapter.notifyItemChanged(position);

    }

    private int findIndexOfLayerIdentity(String layerIdentity) {
        int result = -1;
        for (int x = 0; x < selectedLayerIdentitis.size(); x++) {
            if (selectedLayerIdentitis.get(x).equals(layerIdentity)) {
                result = x;
            }
        }
        return result;
    }

    private final static String KEY_RESTORE_MEMBERS = "SelectMemberPresenter.SelectMembers";

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_RESTORE_MEMBERS)) {
            ArrayList<DialogItem> restoredData = savedInstanceState.getParcelableArrayList(KEY_RESTORE_MEMBERS);
            memberAdapter.clear();
            memberAdapter.addAll(restoredData);
        }

    }

    @Override public void onSave(@NonNull Bundle outState) {
        ArrayList<DialogItem> items = new ArrayList<>(memberAdapter.getItems());
        outState.putParcelableArrayList(KEY_RESTORE_MEMBERS, items);
    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }

    @Override public void searchText(String string) {
        if (string != null) {
            memberAdapter.getFilter().filter(string);
            filterQuery = string;
        } else {
            memberAdapter.getFilter().filter("");
        }
    }

    @Override public void done() {
        if (!selectedLayerIdentitis.isEmpty()) {
            String[] layerIdentityArray = selectedLayerIdentitis.toArray(new String[selectedLayerIdentitis.size()]);
            // TODO: 28.02.17 (alexeenkoff) need calculate screenTitle, if will use group conversation for parent app
            activitySwitcher.open(new ConversationActivity.Screen(layerIdentityArray, /*screenTitle*/StringUtils.EMPTY));
            ;
        }

    }


}
