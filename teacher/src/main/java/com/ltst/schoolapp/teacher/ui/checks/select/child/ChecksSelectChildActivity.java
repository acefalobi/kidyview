package com.ltst.schoolapp.teacher.ui.checks.select.child;

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
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivityScope.ChecksSelectChildActivityModule;
import com.ltst.schoolapp.teacher.ui.checks.select.child.fragment.ChecksSelectChildFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChecksSelectChildActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<ChecksSelectChildActivityScope.ChecksSelectChildActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private ChecksSelectChildActivityScope.ChecksSelectChildActivityComponent component;
    private boolean isCheckIn;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onExtractParams(Bundle params) {
        isCheckIn = params.getBoolean(Screen.KEY_IS_CHECK_IN);
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
        component = DaggerChecksSelectChildActivityScope_ChecksSelectChildActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .checksSelectChildActivityModule(new ChecksSelectChildActivityModule(getIntent()
                        .getExtras(),getDialogProvider()))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new ChecksSelectChildFragment.Screen(isCheckIn));
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
    public ChecksSelectChildActivityScope.ChecksSelectChildActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_IS_CHECK_IN = "SelectPersonActivity.Screen.isCheckIn";
        private final boolean isCheckIn;

        public Screen(boolean isCheckIn) {
            this.isCheckIn = isCheckIn;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_IS_CHECK_IN, isCheckIn);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChecksSelectChildActivity.class;
        }
    }


}
