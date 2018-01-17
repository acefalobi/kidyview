package com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.uimodel.ChecksSelectMemberModel;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.checks.code.ChecksCodeActivity;
import com.ltst.schoolapp.teacher.ui.checks.other.ChecksOtherActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivity;
import com.ltst.schoolapp.teacher.ui.checks.single.check.SingleCheckActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChecksSelectMemberPresenter implements ChecksSelectMemberContract.Presenter,
        ChecksSelectMemberViewHolder.SelectPersonActionListener {

    public static final String KEY_LAST_CHECKED = "SelectPersonPresenter.last.checked";
    private final ChecksSelectMemberContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final List<Long> childrenDBIds;
    private final List<Long> childrenServerIds;
    private final Bundle activityParams;
    private final boolean isCheckIn;
    private final long groupId;
    private SimpleBindableAdapter<ChecksSelectMemberModel> recyclerAdapter;
    private CompositeSubscription subscriptions;
    private ChecksSelectMemberModel lastSelected;

    @Inject
    public ChecksSelectMemberPresenter(ChecksSelectMemberContract.View view,
                                       ActivityScreenSwitcher screenSwitcher,
                                       DataService dataService,
                                       Bundle activityParams) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
        String childrenKey = ChecksSelectMemberActivity.Screen.KEY_SELECTED_CHILDREN;
        List<SelectPersonModel> children = activityParams.getParcelableArrayList(childrenKey);
        childrenDBIds = SelectPersonModel.getDBIdList(children);
        childrenServerIds = SelectPersonModel.getServerIdList(children);
        this.activityParams = activityParams;
        String keyCheckIn = ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN;
        isCheckIn = activityParams.getBoolean(keyCheckIn);
        groupId = activityParams.getLong(ChecksSelectMemberActivity.Screen.KEY_SELECTED_GROOUP);
        recyclerAdapter = new SimpleBindableAdapter<>(
                R.layout.fragment_checks_select_member_item,
                ChecksSelectMemberViewHolder.class);
        recyclerAdapter.setActionListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  START SCREEN  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        view.bindAdapter(recyclerAdapter);
        subscriptions.add(dataService.getMembersForChildrenIds(childrenDBIds, isCheckIn)
                .map(ChecksSelectMemberModel::fromMemberList)
                .doOnNext(models -> {
                    if (isCheckIn) {
                        models.add(ChecksSelectMemberModel.getOther());
                    } else {
                        if (childrenDBIds.size() == 1) {
                            models.add(ChecksSelectMemberModel.getOther());
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bindData, Throwable::printStackTrace));

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
        return onNextClick -> {
            if (lastSelected.getMemberId() == ChecksSelectMemberModel.OTHER_ID && isCheckIn) {
                openChecksOtherScreen(groupId);
            } else {
                if (isCheckIn) {
                    checkIn();
                } else {
                    if (lastSelected.getMemberId() == ChecksSelectMemberModel.OTHER_ID) {
                        openChecksCodeScreen();
                    } else {
                        checkOut();
                    }

                }
            }
        };
    }

    private void openChecksCodeScreen() {
        screenSwitcher.open(new ChecksCodeActivity.Screen(activityParams, this.groupId, null, null));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////  CHECKIN AND CHECKOUT  ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void checkIn() {
        subscriptions.add(Observable.just(Member.createForId(lastSelected.getMemberId()))
                .flatMap(member -> dataService.checkIn(groupId, childrenServerIds, member))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openSingleCheckScreen, Throwable::printStackTrace));
    }

    private void checkOut() {
        subscriptions.add(Observable.just(Member.createForId(lastSelected.getMemberId()))
                .flatMap(member -> dataService.checkOut(groupId, childrenServerIds, member, null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openSingleCheckScreen, Throwable::printStackTrace));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////  ITEMS  ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private Action1<List<ChecksSelectMemberModel>> bindData = new Action1<List<ChecksSelectMemberModel>>() {
        @Override
        public void call(List<ChecksSelectMemberModel> models) {
            recyclerAdapter.clear();
            recyclerAdapter.addAll(models);
            boolean isCheckIn = activityParams.getBoolean(ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN);
            ArrayList<SelectPersonModel> selectedChildren
                    = activityParams.getParcelableArrayList(ChecksSelectMemberActivity.Screen.KEY_SELECTED_CHILDREN);
            view.setHeaderText(isCheckIn, getChildrenNames(selectedChildren), models.size());
        }
    };

    private String getChildrenNames(List<SelectPersonModel> models) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < models.size(); i++) {
            builder.append(models.get(i).getName());
            if (i != models.size() - 1) {
                builder.append(StringUtils.COMMA);
                builder.append(StringUtils.SPACE);
            }
        }
        return builder.toString();
    }

    @Override
    public void OnItemClickListener(int position, ChecksSelectMemberModel item) {
        deselectLast(item);
        lastSelected = item;
        item.setSelected(!item.isSelected());
        recyclerAdapter.notifyItemChanged(position);
        view.setNextEnabled(item.isSelected());
    }

    private synchronized void deselectLast(ChecksSelectMemberModel item) {
        if (item.equals(lastSelected) || lastSelected == null) return;
        lastSelected.setSelected(false);
        recyclerAdapter.notifyItemChanged(recyclerAdapter.indexOf(lastSelected));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////   NAVIGATION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void goBack() {
        screenSwitcher.goBack();
    }

    private void openChecksOtherScreen(Long groupId) {
        activityParams.putParcelable(ChecksSelectMemberActivity.Screen.KEY_SELECTED_MEMBERS, lastSelected);
        screenSwitcher.open(new ChecksOtherActivity.Screen(activityParams, groupId, lastSelected));
    }

    private void openSingleCheckScreen(List<ChildCheck> childChecks) {
        screenSwitcher.open(new SingleCheckActivity.Screen(new ArrayList<>(childChecks)));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  LIFECYCLE  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
        lastSelected = savedInstanceState.getParcelable(KEY_LAST_CHECKED);
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        outState.putParcelable(KEY_LAST_CHECKED, lastSelected);
    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }

}
