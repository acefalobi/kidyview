package com.ltst.schoolapp.teacher.ui.editprofile;

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
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<EditProfileActivityScope.EditProfileActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;
    @Inject ApplicationSwitcher applicationSwitcher;

    private EditProfileActivityScope.EditProfileActivityComponent component;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;

    private Profile profile;
    private int screenMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_EnterTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onExtractParams(Bundle params) {
        profile = params.getParcelable(Screen.KEY_PROFILE);
        screenMode = params.getInt(Screen.KEY_SCREEN_MODE);
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
    protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerEditProfileActivityScope_EditProfileActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .editProfileActivityModule(new EditProfileActivityScope.EditProfileActivityModule(
                        getFragmentScreenSwitcher(),
                        getDialogProvider(),
                        profile,
                        screenMode,
                        new GalleryPictureLoader(this)
                ))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        applicationSwitcher.attach(this);
        screenSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new EditProfileFragment.Screen(screenMode));
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
    public EditProfileActivityScope.EditProfileActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_PROFILE = "EditProfileActivity.Screen.profile";
        public static final String KEY_SCREEN_MODE = "EditProfileActivity.Screen.mode";

        private final Profile profile;
        private final int screenMode;

        public Screen(Profile profile, @EditProfileFragment.ScreenMode int screenMode) {
            this.profile = profile;
            this.screenMode = screenMode;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_PROFILE, profile);
            intent.putExtra(KEY_SCREEN_MODE, screenMode);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return EditProfileActivity.class;
        }
    }



}
