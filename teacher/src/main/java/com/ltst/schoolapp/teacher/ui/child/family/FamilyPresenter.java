package com.ltst.schoolapp.teacher.ui.child.family;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.utils.PhoneUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.preferences.qualifiers.IsAdmin;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.holder.FamilyViewHolder;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.core.util.ActivityProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.child.checkemail.CheckEmailFragment;
import com.ltst.schoolapp.teacher.ui.child.family.status.ChangeStatusItem;
import com.ltst.schoolapp.teacher.ui.child.family.status.ChangeStatusMemberWrapper;
import com.ltst.schoolapp.teacher.ui.child.family.status.ChangeStatusViewHolder;
import com.ltst.schoolapp.teacher.ui.child.family.status.Status;

import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.Result;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FamilyPresenter implements FamilyContract.Presenter {
    private final FamilyContract.View view;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final FamilyAdapter adapter;
    private final DataService dataService;
    private final ActivityProvider activityProvider;
    private final ActivityScreenSwitcher activitySwitcher;
    private final Child child;
    private ChangeStatusMemberWrapper tempMember;
    private CompositeSubscription subscriptions;
    private SimpleBindableAdapter<ChangeStatusItem> changeStatusAdapter
            = new SimpleBindableAdapter<>(R.layout.viewholder_change_member_status, ChangeStatusViewHolder.class);

    @Inject
    public FamilyPresenter(FamilyContract.View view,
                           FragmentScreenSwitcher fragmentSwitcher,
                           DataService dataService,
                           ActivityProvider activityProvider,
                           ActivityScreenSwitcher activitySwitcher,
                           Child child,
                           @IsAdmin BooleanPreference isAdmin,
                           ApplicationSwitcher applicationSwitcher) {
        this.view = view;
        this.fragmentSwitcher = fragmentSwitcher;
        this.dataService = dataService;
        this.activityProvider = activityProvider;
        this.activitySwitcher = activitySwitcher;
        this.child = child;
        FamilyViewHolder.MemberClickListener actionListener = new FamilyViewHolder.MemberClickListener() {
            @Override public void onPhoneClick(Member item) {
                PhoneUtils.dialNumber(activityProvider.getContext(), item.getPhone());
            }

            @Override public void onEmailClick(Member item) {
                applicationSwitcher.openEmailApplication(item.getEmail());
            }

            @Override public void onChangeStatusClick(Member item) {
                changeParentStatus(item);
            }


            @Override public void OnItemClickListener(int position, Member item) {
                String avatarUrl = item.getAvatarUrl();
                if (!StringUtils.isBlank(avatarUrl)) {
                    fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(avatarUrl));
                }
            }
        };
        this.adapter = new FamilyAdapter(isAdmin.get(), actionListener);

    }

    private void changeParentStatus(Member member) {
        tempMember = new ChangeStatusMemberWrapper(member.copy());
        changeStatusAdapter.clear();
        for (Status status : Status.values()) {
            ChangeStatusItem item = new ChangeStatusItem(status,
                    member.getAccessLevel().equals(status.getDefault()));
            changeStatusAdapter.add(item);
        }
        changeStatusAdapter.notifyDataSetChanged();
        changeStatusAdapter.setActionListener((position, item) -> {

            if (tempMember.getMember().getAccessLevel().equals(item.getStatus().getDefault())) {
                tempMember.setWasChanged(false);
            } else {
                tempMember.setWasChanged(true);
                tempMember.getMember().setAccessLevel(item.getStatus().getDefault());
            }
            for (ChangeStatusItem statusItem : changeStatusAdapter.getItems()) {
                if (statusItem.getStatus() == item.getStatus()) {
                    statusItem.setChecked(true);
                } else {
                    statusItem.setChecked(false);
                }
                changeStatusAdapter.notifyDataSetChanged();
            }
        });
        view.showChangeStatusPopup(member, changeStatusAdapter);


    }


    @Override
    public void start() {
        subscriptions = new CompositeSubscription();
        view.setAdapter(adapter);
        List<Member> family = child.getFamily();
        if (family != null && !family.isEmpty()) {
            adapter.addAll(family);
        }
        List<Member> invites = child.getInvites();
        if (invites != null && !invites.isEmpty()) {
            adapter.addAll(invites);
        }
    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
        subscriptions = null;
        adapter.getItems().clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void firstStart() {

    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void goBack() {
        fragmentSwitcher.goBack();
    }

    @Override
    public void openAddMember() {
        fragmentSwitcher.open(new CheckEmailFragment.Screen());
    }

    @Override public void changeStatusForMember() {
        if (tempMember.wasChanged()) {
            view.startLoad();
            subscriptions.add(dataService.changeMemberStatus(tempMember)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(changeStatusAction, Throwable::printStackTrace));
        }
    }

    private Action1<Result<Void>> changeStatusAction = voidResult -> {
        if (voidResult.response().isSuccessful()) {
            FamilyPresenter.this.view.stopLoad();
            FamilyAdapter familyAdapter = FamilyPresenter.this.adapter;
            int adapterPosition = 0;
            List<Member> items = familyAdapter.getItems();
            for (int x = 0; x < items.size(); x++) {
                if (items.get(x).getId() == tempMember.getMember().getId()) {
                    adapterPosition = x;
                    items.set(adapterPosition, tempMember.getMember().copy());
                    break;
                }
            }
            familyAdapter.notifyItemChanged(adapterPosition);
        }
    };

    @Override public void dismissChangeStatus() {
        tempMember = null;
    }
}
