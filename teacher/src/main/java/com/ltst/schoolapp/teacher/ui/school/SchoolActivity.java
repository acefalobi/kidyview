package com.ltst.schoolapp.teacher.ui.school;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.editprofile.EditProfileActivity;
import com.ltst.schoolapp.teacher.ui.school.fragment.SchoolFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

//import com.ltst.schoolapp.teacher.ui.school.fragment.SchoolFragment;

public class SchoolActivity extends TeacherActivity implements HasSubComponents<SchoolScope.SchoolComponent>,
        HasFragmentContainer {

    private SchoolScope.SchoolComponent component;
    private Bundle screenParams;
    @Inject ActivityScreenSwitcher activitySwitcher;
    @Inject ApplicationSwitcher applicationSwitcher;

    @Override protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;
    }

    @Override protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        applicationSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new SchoolFragment.Screen());
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
        super.onStop();
        activitySwitcher.detach(this);
        applicationSwitcher.detach(this);
    }

    @Override protected Toolbar getToolbar() {
        return ButterKnife.findById(this, R.id.default_toolbar);
    }

    @Override public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override public SchoolScope.SchoolComponent getComponent() {
        return component;
    }

    @Override protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerSchoolScope_SchoolComponent.builder()
                .teacherComponent(teacherComponent)
                .schoolModule(new SchoolScope.SchoolModule(screenParams))
                .build();
        component.inject(this);
    }

    public static class Screen extends ActivityScreen {

        private final Profile profile;

        public Screen(Profile profile) {
            this.profile = profile;
        }

        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(EditProfileActivity.Screen.KEY_PROFILE, profile);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return SchoolActivity.class;
        }
    }
}
