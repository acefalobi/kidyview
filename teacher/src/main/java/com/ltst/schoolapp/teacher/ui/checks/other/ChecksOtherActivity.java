package com.ltst.schoolapp.teacher.ui.checks.other;

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
import com.ltst.schoolapp.teacher.ui.checks.other.ChecksOtherActivityScope.ChecksOtherActivityModule;
import com.ltst.schoolapp.teacher.ui.checks.other.fragment.ChecksOtherFragment;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivity.Screen.KEY_SELECTED_MEMBERS;

public class ChecksOtherActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<ChecksOtherActivityScope.ChecksOtherActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private ChecksOtherActivityScope.ChecksOtherActivityComponent component;

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
        component = DaggerChecksOtherActivityScope_ChecksOtherActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .checksOtherActivityModule(new ChecksOtherActivityModule(getIntent().getExtras()))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new ChecksOtherFragment.Screen());
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
    public ChecksOtherActivityScope.ChecksOtherActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        private final boolean isCheckIn;
        private final ArrayList<SelectPersonModel> children;
        private final ChecksSelectMemberModel member;
        private final long selectedGroup;

        public Screen(Bundle previousActivityParams, Long groupId, ChecksSelectMemberModel member) {
            String keyCheckIn = ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN;
            String keySelectedChildren = ChecksSelectMemberActivity.Screen.KEY_SELECTED_CHILDREN;
            String keySelectedGroup = ChecksSelectMemberActivity.Screen.KEY_SELECTED_GROOUP;
            isCheckIn = previousActivityParams.getBoolean(keyCheckIn);
            children = previousActivityParams.getParcelableArrayList(keySelectedChildren);
            this.selectedGroup = previousActivityParams.getLong(keySelectedGroup);
            this.member = member;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN, isCheckIn);
            intent.putExtra(ChecksSelectMemberActivity.Screen.KEY_SELECTED_CHILDREN, children);
            intent.putExtra(ChecksSelectMemberActivity.Screen.KEY_SELECTED_GROOUP, this.selectedGroup);
            if (member != null) {
                intent.putExtra(KEY_SELECTED_MEMBERS, member);
            }
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChecksOtherActivity.class;
        }
    }


}
