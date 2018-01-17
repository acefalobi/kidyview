package com.ltst.schoolapp.teacher.ui.select.dialog.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.widget.Filter;

import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.ui.adapter.dialog.DialogItem;
import com.ltst.core.ui.adapter.dialog.DialogMemberAdapter;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.conversation.ConversationActivity;
import com.ltst.schoolapp.teacher.ui.select.dialog.SelectDialogMemberActivity;

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
        if (screenMode == SelectDialogMemberActivity.START_GROUP_CHAT) {
            view.addDomeMenuItem();
        }
        view.setAdapter(memberAdapter);
        if (subscription.isUnsubscribed()) {
            subscription = new CompositeSubscription();
            getAllChildren();
        }
    }

    @Override public void stop() {
        subscription.unsubscribe();
    }

    @Override public void firstStart() {

        subscription = new CompositeSubscription();
        getAllChildren();
    }


    private void getAllChildren() {
        subscription.add(dataService.getAllChildrenFromDataBase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getChildrenSubscription));
    }

    private Action1<List<Child>> getChildrenSubscription = children -> {
        LongSparseArray<Member> allMembers = getSparseMembers(children);
        if (allMembers.size() == 0) {
            emptyList();
        } else {
            if (SelectMemberPresenter.this.memberAdapter.getItemCount() == 0) {
                addMembersToAdapter(allMembers);
            }

        }

    };


    private LongSparseArray<Member> getSparseMembers(List<Child> children) {
        LongSparseArray<Member> allMembers = new LongSparseArray<>();
        for (Child child : children) {
            List<Member> childMembers = child.getFamily();
            for (Member childMember : childMembers) {
                allMembers.put(childMember.getId(), childMember);
            }
        }
        return allMembers;
    }

    private void emptyList() {

    }

    private void addMembersToAdapter(LongSparseArray<Member> allMembers) {
        List<DialogItem> screenMembers = new ArrayList<>(allMembers.size() + 1);
        if (screenMode == SelectDialogMemberActivity.START_SINGLE_CHAT) {
            Member fakeMember = getFakeMember();
            screenMembers.add(DialogItem.fromMember(fakeMember, true));
        }
        screenMembers.addAll(fromSparseArray(allMembers));
        memberAdapter.getItems().clear();
        memberAdapter.getItems().addAll(screenMembers);
        Filter filter = memberAdapter.getFilter();
        filter.filter(filterQuery == null ? StringUtils.EMPTY : filterQuery);
    }


    private List<DialogItem> fromSparseArray(LongSparseArray<Member> membersSparceArray) {
        List<DialogItem> result = new ArrayList<>(membersSparceArray.size());
        for (int x = 0; x < membersSparceArray.size(); x++) {
            long key = membersSparceArray.keyAt(x);
            Member member = membersSparceArray.get(key);
            result.add(DialogItem.fromMember(member, false));
        }
        return result;
    }

    @NonNull private Member getFakeMember() {
        return new Member(Member.FAKE_MEMBER_ID);
    }


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
        String conversationScreenTitle = dialogItem.getFirstName() + StringUtils.SPACE + dialogItem.getLastName();
        activitySwitcher.open(new ConversationActivity.Screen(layerIds, conversationScreenTitle));
    }

    private void workWithItemInList(int position, DialogItem dialogItem) {
        boolean checked = dialogItem.isChecked();
        memberAdapter.getItems().get(position).setChecked(!checked);
        memberAdapter.notifyItemChanged(position);

    }

    private final static String KEY_RESTORE_MEMBERS = "SelectMemberPresenter.SelectMembers";

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_RESTORE_MEMBERS)) {
            ArrayList<DialogItem> restoredData = savedInstanceState.getParcelableArrayList(KEY_RESTORE_MEMBERS);
            if (restoredData != null) {
                memberAdapter.getItems().clear();
                memberAdapter.getItems().addAll(restoredData);
            }
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
        List<DialogItem> selectedItems = new ArrayList<>();
        for (DialogItem dialogItem : memberAdapter.getItems()) {
            if (dialogItem.isChecked()) {
                selectedItems.add(dialogItem);
            }
        }
//        if (!selectedItems.isEmpty()) {
        String[] layerIdentities = getLayerIdentities(selectedItems);
        String screenTitle = formScreenTitle(selectedItems);
        activitySwitcher.open(new ConversationActivity.Screen(layerIdentities, screenTitle));
    }

//}

    private String[] getLayerIdentities(List<DialogItem> items) {
        List<String> identities = new ArrayList<>(items.size());
        for (DialogItem item : items) {
            identities.add(item.getLayerIdentity());
        }
        String[] layerIdentityArray = identities.toArray(new String[identities.size()]);
        return layerIdentityArray;
    }

    private String formScreenTitle(List<DialogItem> items) {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < items.size(); x++) {
            DialogItem dialogItem = items.get(x);
            result.append(dialogItem.getFirstName().substring(0, 1).toUpperCase());
            result.append(dialogItem.getLastName().substring(0, 1).toUpperCase());
            if (x != items.size() - 1) {
                result.append(StringUtils.COMMA)
                        .append(StringUtils.SPACE);
            }
        }
        return result.toString();
    }
}
