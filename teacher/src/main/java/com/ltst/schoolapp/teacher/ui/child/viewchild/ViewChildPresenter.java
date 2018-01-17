package com.ltst.schoolapp.teacher.ui.child.viewchild;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.activities.add.AddPostActivity;
import com.ltst.schoolapp.teacher.ui.child.family.FamilyFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ViewChildPresenter implements ViewChildContract.Presenter {

    private final ViewChildContract.View view;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final TeacherApplication context;
    private boolean hasNewMembers;

    @Nullable
    private List<Group> childGroups;

    private Child child;
    private CompositeSubscription subscription;
    private String avatarUrl;

    @Inject
    public ViewChildPresenter(ViewChildContract.View view,
                              Child child,
                              DataService dataService,
                              ActivityScreenSwitcher activitySwitcher,
                              FragmentScreenSwitcher fragmentScreenSwitcher,
                              TeacherApplication context,
                              long familyRequestMemberId) {
        this.view = view;
        this.child = child;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.fragmentSwitcher = fragmentScreenSwitcher;
        this.context = context;
        this.hasNewMembers = familyRequestMemberId > 1;
    }

    @Override
    public void start() {
        subscription = new CompositeSubscription();
        if (child.getFirstName() != null && child.getLastName() != null) {
            String title = this.child.getFirstName() + StringUtils.SPACE + this.child.getLastName();
            view.setToolbarTitle(title);
        }

        view.startLoad();
        if (child.getId() != 0) {
            subscription.add(dataService.getChildById(child.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::updateChild, this::loadDataThrowable));
        } else if (child.getServerId() != 0) { // open screen from push
            view.startLoad();
            subscription.add(dataService.getChildByServerId(child.getServerId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::updateChild, this::loadDataThrowable));
        }

    }

    private void updateChild(Child child) {
        view.stopLoad();
        this.avatarUrl = child.getAvatarUrl();
        this.child.setServerId(child.getServerId());
        this.child.setName(child.getFirstName());
        this.child.setLastName(child.getLastName());
        this.child.setAvatarUrl(child.getAvatarUrl());
        String gender = child.getGender();
        this.child.setGender(gender.equals(Child.FEMALE) ? Child.FEMALE : Child.MALE);
        this.child.setBirthDay(child.getBirthDay());
        this.child.setBloodGroup(child.getBloodGroup());
        this.child.setGenotype(child.getGenotype());
        this.child.setAllergies(child.getAllergies());
        this.child.setFamilyMembers(child.getFamily());
        this.child.setInviteMembers(child.getInvites());
        this.child.setGroupIds(child.getGroupIds());
        String title = this.child.getFirstName() + StringUtils.SPACE + this.child.getLastName();
        view.setToolbarTitle(title);
        fillView(child);
        fillGroups(child);
    }

    private void loadDataThrowable(Throwable throwable) {
        view.stopLoad();
        if (throwable instanceof NetErrorException) {
            view.networkError();
        }
    }

    private void fillGroups(Child child) {
        subscription.add(dataService.getCachedGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groups -> {
                    List<Long> groupIds = child.getGroupIds();
                    List<Group> childGroups = new ArrayList<>(groupIds.size());
                    for (Group group : groups) {
                        for (Long along : groupIds) {
                            if (group.getId() == along) {
                                childGroups.add(group);
                            }
                        }
                    }
                    if (needChangeGroups(childGroups)) {
                        this.childGroups = childGroups;
                        view.setGroups(childGroups);
                    }

                }));
    }

    @Override
    public void stop() {
        subscription.unsubscribe();
        subscription = null;
    }

    @Override
    public void firstStart() {

    }

    private boolean needChangeGroups(List<Group> newGroups) {
        if (childGroups == null) {
            return true;
        }
        if (childGroups.size() != newGroups.size()) {
            return true;
        } else {
            for (int x = 0; x < childGroups.size(); x++) {
                if (!childGroups.get(x).equals(newGroups.get(x))) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final String KEY_NEW_MEMBERS = "ViewChildPresenter.hasNewMember";

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_NEW_MEMBERS)) {
            hasNewMembers = savedInstanceState.getBoolean(KEY_NEW_MEMBERS);
        }
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        outState.putBoolean(KEY_NEW_MEMBERS, hasNewMembers);
    }

    private void fillView(Child child) {

        view.setAvatar(child.getAvatarUrl());
        view.setFirstName(child.getFirstName());
        view.setLastName(child.getLastName());
        int familyAndIvitesCount = getMembersCount(child);
        view.setFamilyPeopleCount(familyAndIvitesCount, hasNewMembers);

        view.setAgeAndGender(child.getBirthDay(), child.getGender());
        view.setBirthday(child.getBirthDay());
        view.setInfo(child.getAdditional());
        view.setAllergies(child.getAllergies());
        view.setBloodInfo(child.getBloodGroup(), child.getGenotype());


    }

    private int getMembersCount(Child child) {
        List<Member> family = child.getFamily();

        int familyAndIvitesCount = 0;
        if (family != null) {
            familyAndIvitesCount = familyAndIvitesCount + family.size();
        }
        List<Member> invites = child.getInvites();
        if (invites != null) {
            familyAndIvitesCount = familyAndIvitesCount + invites.size();
        }
        return familyAndIvitesCount;
    }

    @Override
    public void goBack() {
        activitySwitcher.goBack();
    }

    @Override
    public void openFamily() {
        if (hasNewMembers) {
            hasNewMembers = false;
            view.setFamilyPeopleCount(getMembersCount(this.child), hasNewMembers);
        }
        fragmentSwitcher.open(new FamilyFragment.Screen());
    }

    @Override
    public void openAvatarPhoto() {
        if (!StringUtils.isBlank(avatarUrl)) {
            fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(avatarUrl));
        }

    }

    @Override
    public void openAddActivity() {
        if (!netAvailable()) {
            view.networkError();
            return;
        }
        long serverId = child.getServerId();
        if (serverId != 0) {
            activitySwitcher.open(new AddPostActivity.Screen(child));
        } else {
            view.startLoad();
            subscription.add(dataService.syncChildWithServer(child)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(child1 -> {
                        view.stopLoad();
                        activitySwitcher.open(new AddPostActivity.Screen(child1));
                    }, throwable -> {
                        if (throwable instanceof NetErrorException) {
                            view.networkError();
                        }
                    }));
        }

    }

    private boolean netAvailable() {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

}
