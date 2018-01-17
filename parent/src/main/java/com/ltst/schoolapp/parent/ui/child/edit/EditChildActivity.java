package com.ltst.schoolapp.parent.ui.child.edit;

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
import com.ltst.schoolapp.parent.ui.child.edit.fragment.EditChildFragment;

import javax.inject.Inject;

public class EditChildActivity extends ParentActivity implements HasFragmentContainer,
        HasSubComponents<EditChildScope.EditChildComponent> {

    private EditChildScope.EditChildComponent component;
    private ParentChild parentChild;

    @Inject ApplicationSwitcher applicationSwitcher;

    @Inject ActivityScreenSwitcher activityScreenSwitcher;

    @Override
    protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.parentChild = params.getParcelable(Screen.EDIT_CHILD_KEY);
    }

    @Override
    protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerEditChildScope_EditChildComponent.builder()
                .editChildModule(new EditChildScope.EditChildModule(parentChild, getDialogProvider(),
                        new GalleryPictureLoader(this), getActivityProvider()))
                .parentComponent(component)
                .build();
        this.component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityScreenSwitcher.attach(this);
        applicationSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new EditChildFragment.Screen());
        }
    }

    @Override
    protected void onStop() {
        activityScreenSwitcher.detach(this);
        applicationSwitcher.detach(this);
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
        return R.layout.activity_edit_child;
    }

    @Override
    protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.edit_child_toolbar));
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.edit_child_fragment_container;
    }

    @Override
    public EditChildScope.EditChildComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        public static final String EDIT_CHILD_KEY = "EditChildActivity.Screen.Key";
        private final ParentChild parentChild;

        public Screen(ParentChild child) {
            this.parentChild = child;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(EDIT_CHILD_KEY, parentChild);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return EditChildActivity.class;
        }
    }
}
