package com.ltst.schoolapp.teacher.ui.checks.select.family.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivityScope.ChecksSelectMemberActivityModule;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment.ChecksSelectMemberFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChecksSelectMemberActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<ChecksSelectMemberActivityScope.ChecksSelectMemberActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private ChecksSelectMemberActivityScope.ChecksSelectMemberActivityComponent component;

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
        component = DaggerChecksSelectMemberActivityScope_ChecksSelectMemberActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .checksSelectMemberActivityModule(new ChecksSelectMemberActivityModule(getIntent()
                        .getExtras()))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new ChecksSelectMemberFragment.Screen());
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
    public ChecksSelectMemberActivityScope.ChecksSelectMemberActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_SELECTED_CHILDREN = "ChecksSelectMemberActivity.Screen.models";
        public static final String KEY_SELECTED_GROOUP ="ChecksSelectMemberActivity.Screen.group";
        public static final String KEY_SELECTED_MEMBERS = "ChecksOtherActivity.Screen.models";

        private final boolean isCheckIn;
        private final ArrayList<SelectPersonModel> models;
        private final long selectedGroupId;

        public Screen(Bundle previousActivityParams, long selectedGroupId, ArrayList<SelectPersonModel> models) {
            String key = ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN;
            isCheckIn = previousActivityParams.getBoolean(key);
            this.models = models;
            this.selectedGroupId = selectedGroupId;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN, isCheckIn);
            if (models != null) {
                intent.putExtra(KEY_SELECTED_CHILDREN, models);
            }
            intent.putExtra(ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN, isCheckIn);
            intent.putExtra(KEY_SELECTED_GROOUP,selectedGroupId);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChecksSelectMemberActivity.class;
        }
    }


}
