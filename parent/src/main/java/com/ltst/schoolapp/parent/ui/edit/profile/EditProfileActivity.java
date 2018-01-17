package com.ltst.schoolapp.parent.ui.edit.profile;

import android.content.Intent;
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
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.edit.profile.fragment.EditProfileFragment;

import javax.inject.Inject;

public class EditProfileActivity extends ParentActivity
        implements HasSubComponents<EditProfileScope.EditProfileComponent>, HasFragmentContainer {

    private EditProfileScope.EditProfileComponent component;

    @Inject
    ApplicationSwitcher applicationSwitcher;

    @Inject ActivityScreenSwitcher activityScreenSwitcher;

    @Override protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerEditProfileScope_EditProfileComponent.builder()
                .editProfileModule(new EditProfileScope.EditProfileModule(getDialogProvider(),
                        new GalleryPictureLoader(this)))
                .parentComponent(component)
                .build();
        this.component.inject(this);
    }

    @Override protected void onStart() {
        super.onStart();
        applicationSwitcher.attach(this);
        activityScreenSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new EditProfileFragment.Screen());
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override protected void onStop() {
        applicationSwitcher.detach(this);
        activityScreenSwitcher.detach(this);
        super.onStop();
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

    @Override public EditProfileScope.EditProfileComponent getComponent() {
        return this.component;
    }

    public static class Screen extends ActivityScreen {

        @Override protected void configureIntent(@NonNull Intent intent) {

        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return EditProfileActivity.class;
        }
    }
}
