package com.ltst.schoolapp.teacher.ui.events.add;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.events.add.fragment.AddEventFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEventActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<AddEventActivityScope.AddEventActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;
    @Inject
    ApplicationSwitcher applicationSwitcher;

    private AddEventActivityScope.AddEventActivityComponent component;
    private Bundle screenParams;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onExtractParams(Bundle params) {
        this.screenParams = params;
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
        component = DaggerAddEventActivityScope_AddEventActivityComponent.builder()
                .addEventActivityModule(
                        new AddEventActivityScope.AddEventActivityModule(this, screenParams))
                .teacherComponent(teacherComponent)
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        screenSwitcher.attach(this);
        applicationSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new AddEventFragment.Screen());
        }
    }

    @Override
    protected void onStop() {
        applicationSwitcher.detach(this);
        screenSwitcher.detach(this);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public AddEventActivityScope.AddEventActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        public static final String SELECTED_YEAR = "AddEventActivity.SelectedYear";
        public static final String SELECTED_MONTH = "AddEventActivity.SelectedMonth";
        public static final String SELECTED_DAY = "AddEventActivity.SelectedDay";

        private final int year;
        private final int month;
        private final int day;

        public Screen(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(SELECTED_YEAR, year);
            intent.putExtra(SELECTED_MONTH, month);
            intent.putExtra(SELECTED_DAY, day);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return AddEventActivity.class;
        }
    }


}
