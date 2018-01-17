package com.ltst.schoolapp.parent.ui.child;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.child.view.ViewChildFragment;

import javax.inject.Inject;

public class ChildActivity extends ParentActivity implements HasFragmentContainer,
        HasSubComponents<ChildScope.ChildComponent> {

    private ChildScope.ChildComponent component;
    private Bundle screenParams;

    @Inject
    ActivityScreenSwitcher activitySwitcher;

    @Inject ApplicationSwitcher applicationSwitcher;

    @Override
    protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;
    }

    @Override
    protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerChildScope_ChildComponent.builder()
                .parentComponent(component)
                .childModule(new ChildScope.ChildModule(screenParams,
                        getFragmentScreenSwitcher(),
                        getActivityProvider(), getDialogProvider(),
                        new GalleryPictureLoader(this)))
                .build();
        this.component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        applicationSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new ViewChildFragment.Screen());
        }
    }

    @Override
    protected void onStop() {
        applicationSwitcher.detach(this);
        activitySwitcher.detach(this);
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
    protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override
    protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.default_toolbar));
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public ChildScope.ChildComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        public static final String KEY_ITEM_CHILD = "ChildActivity.ItemParentChild";
        public static final String KEY_CAN_EDIT = "ChildActivity.CanEdit";

        private final ParentChild child;
        private final boolean canEdit;

        public Screen(ParentChild item, boolean canEdit) {
            this.child = item;
            this.canEdit = canEdit;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_ITEM_CHILD, child);
            intent.putExtra(KEY_CAN_EDIT, canEdit);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChildActivity.class;
        }
    }
}
