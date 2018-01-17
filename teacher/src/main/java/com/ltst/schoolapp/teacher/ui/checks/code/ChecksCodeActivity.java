package com.ltst.schoolapp.teacher.ui.checks.code;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.uimodel.ChecksSelectMemberModel;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.checks.code.ChecksCodeActivityScope.ChecksCodeActivityModule;
import com.ltst.schoolapp.teacher.ui.checks.code.fragment.ChecksCodeFragment;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChecksCodeActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<ChecksCodeActivityScope.ChecksCodeActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private ChecksCodeActivityScope.ChecksCodeActivityComponent component;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onExtractParams(Bundle params) {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.default_activity_green;
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerChecksCodeActivityScope_ChecksCodeActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .checksCodeActivityModule(new ChecksCodeActivityModule(getIntent().getExtras()))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new ChecksCodeFragment.Screen());
        }
        screenSwitcher.attach(this);
    }

    @Override
    protected void onStop() {
        screenSwitcher.detach(this);
        super.onStop();
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public ChecksCodeActivityScope.ChecksCodeActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_FIRST_NAME = "ChecksCodeActivity.Screen.firstName";
        public static final String KEY_LAST_NAME = "ChecksCodeActivity.lastName.models";

        private final boolean isCheckIn;
        private final ArrayList<SelectPersonModel> children;
        private final ChecksSelectMemberModel member;
        private final String firstName;
        private final String lastName;
        private final long selectedGroupId;

        public Screen(Bundle previousActivityParams, long groupId , String firstName, String lastName) {
            String keyCheckIn = ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN;
            String keySelectedChildren = ChecksSelectMemberActivity.Screen.KEY_SELECTED_CHILDREN;
            String keyMember = ChecksSelectMemberActivity.Screen.KEY_SELECTED_MEMBERS;
            isCheckIn = previousActivityParams.getBoolean(keyCheckIn);
            children = previousActivityParams.getParcelableArrayList(keySelectedChildren);
            this.member = previousActivityParams.getParcelable(keyMember);
            this.firstName = firstName;
            this.lastName = lastName;
            this.selectedGroupId = groupId;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN, isCheckIn);
            intent.putExtra(ChecksSelectMemberActivity.Screen.KEY_SELECTED_CHILDREN, children);
            intent.putExtra(ChecksSelectMemberActivity.Screen.KEY_SELECTED_MEMBERS, member);
            intent.putExtra(ChecksSelectMemberActivity.Screen.KEY_SELECTED_GROOUP, selectedGroupId);
            if (firstName != null) {
                intent.putExtra(KEY_FIRST_NAME, firstName);
            }
            if (lastName != null) {
                intent.putExtra(KEY_LAST_NAME, lastName);
            }
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChecksCodeActivity.class;
        }
    }


}
