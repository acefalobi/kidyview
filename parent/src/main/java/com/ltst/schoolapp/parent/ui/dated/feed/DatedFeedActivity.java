package com.ltst.schoolapp.parent.ui.dated.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.ChildInGroup;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.main.feed.FeedFragment;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatedFeedActivity extends ParentActivity implements HasFragmentContainer,
        HasSubComponents<DatedFeedScope.DatedFeedComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;
    @Inject
    ApplicationSwitcher applicationSwitcher;
    @Inject
    FragmentScreenSwitcher fragmentScreenSwitcher;

    private DatedFeedScope.DatedFeedComponent component;
    private Bundle screenParams;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_EnterTheme);
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
    protected void addToParentComponent(ParentScope.ParentComponent parentComponent) {
        component = DaggerDatedFeedScope_DatedFeedComponent.builder()
                .datedFeedModule(new DatedFeedScope.DatedFeedModule(this, screenParams))
                .parentComponent(parentComponent)
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        screenSwitcher.attach(this);
        applicationSwitcher.attach(this);
        fragmentScreenSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new FeedFragment.Screen());
        }
    }

    @Override
    protected void onStop() {
        fragmentScreenSwitcher.detach();
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
    public DatedFeedScope.DatedFeedComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_DATE = "DatedFeedActivity.Screen.date";
        public static final String KEY_CHILD_IN_GROUP = "DatedFeedActivity.Screen.childINGroup";

        private Calendar date;
        private final ChildInGroup selectedChildInGroup;

        public Screen(Calendar date, ChildInGroup selectedChildInGroup) {
            this.date = date;

            this.selectedChildInGroup = selectedChildInGroup;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_DATE, date);
            intent.putExtra(KEY_CHILD_IN_GROUP, selectedChildInGroup);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return DatedFeedActivity.class;
        }
    }


}