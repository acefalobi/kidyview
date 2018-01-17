package com.ltst.schoolapp.parent.ui.checkout.select.school;


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
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.model.ChildInGroupInSchool;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.checkout.select.school.fragment.SelectChildInSchoolFragment;

import java.util.ArrayList;

import javax.inject.Inject;

public class SelectChildInSchoolActivity extends ParentActivity implements HasFragmentContainer,
        HasSubComponents<SelectSchoolScope.SelectSchoolComponent> {

    @Inject ActivityScreenSwitcher activitySwitcher;

    private SelectSchoolScope.SelectSchoolComponent component;
    private Bundle screenParams;

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        screenParams = params;
    }

    @Override protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerSelectSchoolScope_SelectSchoolComponent.builder()
                .parentComponent(component)
                .selectSchoolModule(new SelectSchoolScope.SelectSchoolModule(screenParams))
                .build();
        this.component.inject(this);
    }

    @Override protected int getLayoutResId() {
        return R.layout.default_activity_green;
    }

    @Override protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.default_toolbar));
    }


    @Override public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override public SelectSchoolScope.SelectSchoolComponent getComponent() {
        return component;
    }

    @Override protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new SelectChildInSchoolFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activitySwitcher.detach(this);
    }

    public static class Screen extends ActivityScreen {

        public static final String KEY_CHILD_IN_GROUP_IN_SCHOOL =
                "SelectChildInSchoolActivity.Screen.ParentChildren";
        public static final String KEY_SELECTED_OBJECT = "SelectChildInSchoolActivity.Screen.Selected";

        private final ArrayList<ChildInGroupInSchool> objects;

        public Screen(ArrayList<ChildInGroupInSchool> objects) {
            this.objects = objects;
        }

        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.putParcelableArrayListExtra(KEY_CHILD_IN_GROUP_IN_SCHOOL, objects);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return SelectChildInSchoolActivity.class;
        }
    }
}
