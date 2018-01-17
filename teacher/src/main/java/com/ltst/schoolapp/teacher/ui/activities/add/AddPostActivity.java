package com.ltst.schoolapp.teacher.ui.activities.add;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.Child;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.activities.add.fragment.AddPostFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddPostActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<AddPostActivityScope.AddPostActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;
    @Inject
    ApplicationSwitcher applicationSwitcher;
    private AddPostActivityScope.AddPostActivityComponent component;
    private Bundle screenParams;
    @BindView(R.id.default_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.GreenTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onExtractParams(Bundle params) {
        screenParams = params;
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
    protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerAddPostActivityScope_AddPostActivityComponent.builder()
                .addPostActivityModule(
                        new AddPostActivityScope.AddPostActivityModule(this, screenParams))
                .teacherComponent(teacherComponent)
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        screenSwitcher.attach(this);
        applicationSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new AddPostFragment.Screen());
        }
    }

    @Override
    protected void onStop() {
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
    public AddPostActivityScope.AddPostActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        public static final String ADD_ACTIVITY_FOR_CHILD_KEY = "AddPostActivity.Screen.ChildId";
        private Child child;

        public Screen() {
        }

        public Screen(Child child) {
            this.child = child;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(ADD_ACTIVITY_FOR_CHILD_KEY, child);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return AddPostActivity.class;
        }
    }


}
