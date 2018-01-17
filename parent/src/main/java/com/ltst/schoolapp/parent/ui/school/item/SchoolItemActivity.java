package com.ltst.schoolapp.parent.ui.school.item;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.school.SchoolInfoWrapper;
import com.ltst.schoolapp.parent.ui.school.item.fragment.ItemFragment;

import javax.inject.Inject;

public class SchoolItemActivity extends ParentActivity implements HasSubComponents<SchoolItemScope.SchoolItemComponent>,
        HasFragmentContainer {

    private SchoolItemScope.SchoolItemComponent component;
    private Bundle screenParams;

    @Inject ActivityScreenSwitcher activitySwitcher;
    @Inject ApplicationSwitcher applicationSwitcher;

    @Override protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerSchoolItemScope_SchoolItemComponent.builder()
                .parentComponent(component)
                .schoolItemModule(new SchoolItemScope.SchoolItemModule(screenParams))
                .build();
        this.component.inject(this);
    }

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;
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

    @Override public SchoolItemScope.SchoolItemComponent getComponent() {
        return component;
    }

    @Override protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        applicationSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new ItemFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activitySwitcher.detach(this);
        applicationSwitcher.detach(this);
    }

    public static final class Screen extends ActivityScreen {
        public static final String INFO_ITEM_KEY = "SchoolItemActivity.Item";

        private final SchoolInfoWrapper wrapper;

        public Screen(SchoolInfoWrapper item) {
            this.wrapper = item;
        }

        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(INFO_ITEM_KEY, wrapper);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return SchoolItemActivity.class;
        }
    }
}
