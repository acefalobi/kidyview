package com.ltst.schoolapp.parent.ui.checkout.select.child;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.Child;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.checkout.select.child.fragment.ChecksSelectChildFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

//import com.ltst.schoolapp.parent.ui.checkout.select.DaggerChecksSelectChildActivityScope_ChecksSelectChildActivityComponent;
//import com.ltst.schoolapp.parent2.ui.checkout.select.child.fragment.ChecksSelectChildFragment;

public class ChecksSelectChildActivity extends ParentActivity implements HasFragmentContainer,
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
        isCheckIn = params.getBoolean(Screen.KEY_SELECTED_IDS);
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
    protected void addToParentComponent(ParentScope.ParentComponent parentComponent) {
        component = DaggerChecksSelectChildActivityScope_ChecksSelectChildActivityComponent.builder()
                .parentComponent(parentComponent)
                .checksSelectChildActivityModule(new ChecksSelectChildActivityScope.ChecksSelectChildActivityModule(getIntent()
                        .getExtras()))
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

        public static final String KEY_SELECTED_IDS = "SelectPersonActivity.Screen.ChildrenCheckedList";
        public static final String KEY_CHILDREN = "SelectPersonActivity.Screen.Children";
        private final ArrayList<Long> selectedIds;
        private final ArrayList<Child> children;

        public Screen(ArrayList<Long> selectedIds, ArrayList<Child> children) {
            this.selectedIds = selectedIds;
            this.children = children;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_SELECTED_IDS, selectedIds);
            intent.putParcelableArrayListExtra(KEY_CHILDREN, children);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChecksSelectChildActivity.class;
        }
    }


}
