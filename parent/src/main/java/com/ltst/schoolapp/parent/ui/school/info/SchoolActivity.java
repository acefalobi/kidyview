package com.ltst.schoolapp.parent.ui.school.info;


import android.content.Intent;
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
import com.ltst.schoolapp.parent.ui.school.info.fragment.SchoolInfoFragment;

import javax.inject.Inject;

public class SchoolActivity extends ParentActivity implements HasFragmentContainer,
        HasSubComponents<SchoolScope.SchoolComponent> {

    private SchoolScope.SchoolComponent component;

    @Inject ActivityScreenSwitcher activitySwitcher;

    @Override protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerSchoolScope_SchoolComponent.builder()
                .schoolModule(new SchoolScope.SchoolModule(getDialogProvider()))
                .parentComponent(component)
                .build();
        this.component.inject(this);
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

    @Override public SchoolScope.SchoolComponent getComponent() {
        return component;
    }

    @Override protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new SchoolInfoFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activitySwitcher.detach(this);
    }

    public static final class Screen extends ActivityScreen {

        @Override protected void configureIntent(@NonNull Intent intent) {

        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return SchoolActivity.class;
        }
    }
}
