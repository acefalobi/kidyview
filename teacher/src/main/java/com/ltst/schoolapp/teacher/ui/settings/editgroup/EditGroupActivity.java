package com.ltst.schoolapp.teacher.ui.settings.editgroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
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
import com.ltst.schoolapp.teacher.ui.settings.editgroup.fragment.EditGroupFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditGroupActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<EditGroupActivityScope.EditGroupActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    @Inject ApplicationSwitcher applicationSwitcher;

    private EditGroupActivityScope.EditGroupActivityComponent component;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;

    private long groupId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_EnterTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onExtractParams(Bundle params) {
        groupId = params.getLong(Screen.KEY_GROUP_ID);
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
        component = DaggerEditGroupActivityScope_EditGroupActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .editGroupActivityModule(new EditGroupActivityScope.EditGroupActivityModule(
                        getDialogProvider(),
                        new GalleryPictureLoader(this), groupId))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        applicationSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new EditGroupFragment.Screen());
        }
        screenSwitcher.attach(this);
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
    protected void onStop() {
        applicationSwitcher.detach(this);
        screenSwitcher.detach(this);
        super.onStop();
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public EditGroupActivityScope.EditGroupActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        private static final String KEY_GROUP_ID = "EditGroupActivity.GroupID";

        private final long groupId;

        public Screen(long groupId) {

            this.groupId = groupId;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_GROUP_ID, groupId);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return EditGroupActivity.class;
        }
    }


}
