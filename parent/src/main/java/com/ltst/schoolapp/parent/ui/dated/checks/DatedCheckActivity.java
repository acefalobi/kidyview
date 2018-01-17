package com.ltst.schoolapp.parent.ui.dated.checks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.main.checks.ChecksFragment;

import javax.inject.Inject;

public class DatedCheckActivity extends ParentActivity implements HasFragmentContainer,
        HasSubComponents<DatedChecksScope.DatedChecksComponent> {

    private DatedChecksScope.DatedChecksComponent component;
    private Bundle screenParams;

    @Inject ActivityScreenSwitcher activityScreenSwitcher;

    @Override protected void addToParentComponent(ParentScope.ParentComponent parentComponent) {
        component = DaggerDatedChecksScope_DatedChecksComponent.builder()
                .parentComponent(parentComponent)
                .datedChecksModule(new DatedChecksScope.DatedChecksModule(screenParams,
                        getFragmentScreenSwitcher(),getDialogProvider()))
                .build();
        component.inject(this);
    }

    @Override protected void onStart() {
        super.onStart();
        activityScreenSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new ChecksFragment.Screen());
        }
    }

    @Override protected void onStop() {
        activityScreenSwitcher.detach(this);
        super.onStop();
    }

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        screenParams = params;
    }

    @Override protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.default_toolbar));
    }

    @Override public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override public DatedChecksScope.DatedChecksComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        public static final String SELECTED_YEAR = "DatedCheckActivity.Year";
        public static final String SELECTED_MONTH = "DatedCheckActivity.Month";
        public static final String SELECTED_DAY = "DatedCheksActivity.Day";

        private final int year;
        private final int month;
        private final int dayOfMonth;

        public Screen(int year, int month, int dayOfMonth) {
            this.year = year;
            this.month = month;
            this.dayOfMonth = dayOfMonth;
        }

        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(SELECTED_YEAR, year);
            intent.putExtra(SELECTED_MONTH, month);
            intent.putExtra(SELECTED_DAY, dayOfMonth);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return DatedCheckActivity.class;
        }
    }
}
