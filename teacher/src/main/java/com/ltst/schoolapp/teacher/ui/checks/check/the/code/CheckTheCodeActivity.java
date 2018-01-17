package com.ltst.schoolapp.teacher.ui.checks.check.the.code;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.checks.check.the.code.CheckTheCodeActivityScope.CheckTheCodeActivityModule;
import com.ltst.schoolapp.teacher.ui.checks.check.the.code.fragment.CheckTheCodeFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckTheCodeActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<CheckTheCodeActivityScope.CheckTheCodeActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private CheckTheCodeActivityScope.CheckTheCodeActivityComponent component;
    private Bundle screenParams;

    @BindView(R.id.default_toolbar) Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
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
        component = DaggerCheckTheCodeActivityScope_CheckTheCodeActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .checkTheCodeActivityModule(new CheckTheCodeActivityModule(getIntent().getExtras()))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new CheckTheCodeFragment.Screen());
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
    public CheckTheCodeActivityScope.CheckTheCodeActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        public static final String KEY_SELECTED_GROUP_TITLE = "CheckTheCodeFragment.Screen.SelectedGroupTitle";
        private final String selectedGroupName;

        public Screen(String selectedGroupName) {
            this.selectedGroupName = selectedGroupName;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_SELECTED_GROUP_TITLE,selectedGroupName);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return CheckTheCodeActivity.class;
        }
    }


}
