package com.ltst.schoolapp.teacher.ui.settings.changepassword;

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
import com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment.ChangePasswordFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<ChangePasswordActivityScope.ChangePasswordActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private ChangePasswordActivityScope.ChangePasswordActivityComponent component;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_EnterTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerChangePasswordActivityScope_ChangePasswordActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new ChangePasswordFragment.Screen());
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
    public ChangePasswordActivityScope.ChangePasswordActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {


        @Override
        protected void configureIntent(@NonNull Intent intent) {
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChangePasswordActivity.class;
        }
    }


}
