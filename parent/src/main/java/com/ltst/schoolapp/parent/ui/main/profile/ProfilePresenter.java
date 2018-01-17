package com.ltst.schoolapp.parent.ui.main.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.preferences.qualifiers.IsFirstStart;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.core.util.SimpleDataObserver;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.ui.checkout.fragment.info.ParentProfile;
import com.ltst.schoolapp.parent.ui.child.ChildActivity;
import com.ltst.schoolapp.parent.ui.edit.profile.EditProfileActivity;
import com.ltst.schoolapp.parent.ui.enter.EnterActivity;
import com.ltst.schoolapp.parent.ui.main.ChildInGroupHelper;
import com.ltst.schoolapp.parent.ui.school.info.SchoolActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter implements ProfileContract.Presenter {

    private final ProfileContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ChildInGroupHelper spinnerHelper;
    private final DataService dataService;
    private SimpleBindableAdapter<ParentChild> adapter;
    private CompositeSubscription subscription;
    private String avatarUrl;
    private ArrayList<Long> childIds = new ArrayList<>();
    private String currentEmail;

    @Inject
    public ProfilePresenter(ProfileContract.View view,
                            ActivityScreenSwitcher activitySwitcher,
                            FragmentScreenSwitcher fragmentSwitcher,
                            DataService dataService, ParentApplication context,
                            ChildInGroupHelper spinnerHelper) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.fragmentSwitcher = fragmentSwitcher;
        this.dataService = dataService;
        this.spinnerHelper = spinnerHelper;
        adapter = new SimpleBindableAdapter<>(R.layout.viewholder_parent_child, ParentChildViewHolder.class);
    }


    @Override
    public void firstStart() {
        adapter.registerAdapterDataObserver(new SimpleDataObserver() {
            @Override public void onAnythingChanges() {
                if (adapter.getItemCount() > 0) {
                    view.stopLoad();
                } else {
                    view.startLoad();
                }
            }
        });
    }

    @Override
    public void start() {
        spinnerHelper.showSpinner(false);
        subscription = new CompositeSubscription();
        view.startLoad();
        view.setAdapter(adapter);
        subscription.add(dataService.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getParentProfile,
                        throwable -> {
                            if (throwable instanceof NetErrorException) {
                                view.networkError();
                            }

                        }));
        adapter.setActionListener((position, item) -> {
            initOnItemClickListener(item);
        });
    }

    private void initOnItemClickListener(ParentChild item) {
        Child child = item.getChild();
        List<Member> family = child.getFamily();
        List<Member> invites = child.getInvites();
        ArrayList<Member> allFamilyMembers = new ArrayList<Member>();
        if (family != null) {
            allFamilyMembers.addAll(family);
        }
        if (invites != null) {
            allFamilyMembers.addAll(invites);
        }
        boolean canEdit = false;
        for (Member familyMember : allFamilyMembers) {
            String accessLevel = familyMember.getAccessLevel();
            String email = familyMember.getEmail();
            if (accessLevel.equals(Member.FULL_ACCESS) && email.equals(currentEmail)) {
                canEdit = true;
            }
        }
        activitySwitcher.open(new ChildActivity.Screen(item, canEdit));
    }


    private Action1<ParentProfile> getParentProfile = parentProfile -> {
        Profile profile = parentProfile.getProfile();
        currentEmail = profile.getEmail();
        this.avatarUrl = profile.getAvatarUrl();
        ProfilePresenter.this.view.bindProfileData(profile);
        List<ParentChild> newItems = parentProfile.getChildList();
        if (newItems != null) {
            ProfilePresenter.this.view.stopLoad();
            List<ParentChild> oldItems = adapter.getItems();
            if (oldItems == null) {
                oldItems = new ArrayList<>();
            }
            final ParentChild.DiffCallBack callBack = new ParentChild.DiffCallBack(oldItems, newItems);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack);
            oldItems.clear();
            oldItems.addAll(newItems);
            diffResult.dispatchUpdatesTo(adapter);
        }

    };


    @Override
    public void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }


    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void openEditProfile() {
        activitySwitcher.open(new EditProfileActivity.Screen());
    }

    @Override
    public void openAvatarPhoto() {
        if (!StringUtils.isBlank(avatarUrl)) {
            fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(avatarUrl));
        }
    }

    @Override
    public void logout() {
        subscription.add(dataService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    activitySwitcher.open(new EnterActivity.Screen());
                }, throwable -> {
                    if (throwable instanceof NetErrorException) {
                        view.networkError();
                    }
                }));
    }

    @Override public void openSchoolScreen() {
        activitySwitcher.open(new SchoolActivity.Screen());
    }
}
