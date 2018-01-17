package com.ltst.schoolapp.parent.ui.child.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.utils.PhoneUtils;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.holder.FamilyViewHolder;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.ui.child.edit.fragment.EditChildFragment;
import com.ltst.schoolapp.parent.ui.family.add.AddMemberActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ViewChildPresenter implements ViewChildContract.Presenter {


    private final ViewChildContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final ParentChild parentChild;
    private final boolean canEditChild;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ApplicationSwitcher applicationSwitcher;
    private final ParentApplication context;

    private SimpleBindableAdapter<Member> familyAdapter = new SimpleBindableAdapter<>(R.layout.viewholder_member,
            FamilyViewHolder.class);

    @Inject
    public ViewChildPresenter(ViewChildContract.View view,
                              ActivityScreenSwitcher activitySwitcher,
                              ParentChild parentChild,
                              boolean canEditChild, FragmentScreenSwitcher fragmentSwitcher,
                              ApplicationSwitcher applicationSwitcher,
                              ParentApplication context) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.parentChild = parentChild;
        this.canEditChild = canEditChild;
        this.fragmentSwitcher = fragmentSwitcher;
        this.applicationSwitcher = applicationSwitcher;
        this.context = context;
    }

    @Override
    public void firstStart() {
        List<Group> groups = parentChild.getChild().getGroups();
        view.setGroups(groups);
    }

    @Override
    public void start() {
        view.bindData(parentChild, canEditChild);
        view.setFamilyAdapter(familyAdapter, canEditChild);
        familyAdapter.setActionListener(new FamilyViewHolder.MemberClickListener() {
            @Override
            public void onPhoneClick(Member item) {
                PhoneUtils.dialNumber(context, item.getPhone());
            }

            @Override
            public void onEmailClick(Member item) {
                applicationSwitcher.openEmailApplication(item.getEmail());
            }

            @Override
            public void onChangeStatusClick(Member item) {
                //nothing for parent application
            }

            @Override
            public void OnItemClickListener(int position, Member item) {
                String avatarUrl = item.getAvatarUrl();
                if (!StringUtils.isBlank(avatarUrl)) {
                    fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(avatarUrl));
                }
            }
        });
        fillFamily();

    }

    private void fillFamily() {
        Child child = parentChild.getChild();
        int familyCount = 0;
        List<Member> family = child.getFamily();
        if (family != null) {
            familyCount = familyCount + family.size();
        }
        List<Member> invites = child.getInvites();
        if (invites != null) {
            familyCount = familyCount + invites.size();
        }
        ArrayList<Member> newMembers = new ArrayList<>(familyCount);
        if (family != null) {
            newMembers.addAll(family);
        }
        if (invites != null) {
            newMembers.addAll(invites);
        }
        if (familyAdapter.getItemCount() > 0) {
            List<Member> oldItems = familyAdapter.getItems();
            Member.DiffCallback diffCallback = new Member.DiffCallback(oldItems, newMembers);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            oldItems.clear();
            oldItems.addAll(newMembers);
            diffResult.dispatchUpdatesTo(familyAdapter);
        } else familyAdapter.addAll(newMembers);
    }

    @Override
    public void stop() {

    }

    private static final String KEY_CHILD_FAMILY = "ViewChildPresenter.ChildFamily";
    private static final String KEY_CHILD_GROUPS = "ViewChildPresenter.ChildGroups";

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_CHILD_FAMILY)) {
            ArrayList<Member> result = savedInstanceState.getParcelableArrayList(KEY_CHILD_FAMILY);
            if (result != null) {
                familyAdapter.clear();
                familyAdapter.addAll(result);
            }

        }
        if (savedInstanceState.containsKey(KEY_CHILD_GROUPS)) {
            ArrayList<Group> groups = savedInstanceState.getParcelableArrayList(KEY_CHILD_GROUPS);
            if (groups != null) {
                view.setGroups(groups);
            }
        }
    }


    @Override
    public void onSave(@NonNull Bundle outState) {
        if (familyAdapter.getItemCount() != 0) {
            List<Member> members = familyAdapter.getItems();
            ArrayList<Member> forSave = new ArrayList<>(members);
            outState.putParcelableArrayList(KEY_CHILD_FAMILY, forSave);
        }
        List<Group> groups = parentChild.getChild().getGroups();
        if (groups.size() != 0) {
            ArrayList<Group> groupsFroSave = new ArrayList<>(groups);
            outState.putParcelableArrayList(KEY_CHILD_GROUPS, groupsFroSave);
        }

    }

    @Override
    public void goBack() {
        activitySwitcher.goBack();
    }

    @Override
    public void openEditChildScreen() {
        fragmentSwitcher.open(new EditChildFragment.Screen());
    }

    @Override
    public void openAvatar() {
        String avatarUrl = parentChild.getChild().getAvatarUrl();
        if (!StringUtils.isBlank(avatarUrl)) {
            fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(avatarUrl));
        }
    }

    @Override
    public void familyRequest() {
        long childId = parentChild.getChild().getServerId();
        int schoolId = parentChild.getSchoolId();
        String childFirstName = parentChild.getChild().getFirstName();
        String childLastName = parentChild.getChild().getLastName();
        ArrayList<String> existParentsEmails = getExistParentsEmails();
        AddMemberActivity.Screen screen = new AddMemberActivity.Screen(childId, schoolId,
                childFirstName, childLastName, existParentsEmails);
        activitySwitcher.startForResult(screen, AddMemberActivity.Screen.RESULT_FAMILY_MEMBER_REQUEST_CODE);
    }

    private ArrayList<String> getExistParentsEmails() {
        List<Member> allMembers = parentChild.getChild().getAllMembers();
        ArrayList<String> allMembersEmails = new ArrayList<>(allMembers.size());
        for (Member member : allMembers) {
            allMembersEmails.add(member.getEmail());
        }
        return allMembersEmails;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AddMemberActivity.Screen.RESULT_FAMILY_MEMBER_REQUEST_CODE) {
                Member createdMember =
                        data.getParcelableExtra(AddMemberActivity.Screen.RESULT_FAMILY_MEMBER_KEY);
                if (createdMember != null) {
                    List<Member> invites = this.parentChild.getChild().getInvites();
                    if (invites != null) {
                        invites.add(createdMember);
                        this.parentChild.getChild().setInviteMembers(invites);
                    } else {
                        List<Member> newInvtes = new ArrayList<>();
                        newInvtes.add(createdMember);
                        this.parentChild.getChild().setInviteMembers(newInvtes);
                    }
                    fillFamily();
                }
            }
        }
    }
}
