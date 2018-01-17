package com.ltst.schoolapp.teacher.ui.addchild;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.ltst.schoolapp.teacher.ui.addchild.fragment.AddChildFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddChildActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<AddChildScope.TempComponent> {

    @BindView(R.id.default_toolbar) Toolbar toolbar;

    private AddChildScope.TempComponent component;

    @Inject ActivityScreenSwitcher activitySwitcher;
    @Inject ApplicationSwitcher applicationSwitcher;

    private int childOdForEdit = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

    }

    @Override
    protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        childOdForEdit = params.getInt(AddChildFragment.Screen.EDIT_CHILD_ID, 0);
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
        component = DaggerAddChildScope_TempComponent.builder()
                .addChildModule(new AddChildScope.AddChildModule(
                        getActivityProvider(),
                        new GalleryPictureLoader(this),
                        getDialogProvider(), childOdForEdit))
                .teacherComponent(teacherComponent)
                .build();
        component.inject(this);
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public AddChildScope.TempComponent getComponent() {
        return component;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        applicationSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            if (childOdForEdit == 0) {
                //CREATE CHILD
                fragmentScreenSwitcher.open(new AddChildFragment.Screen());
            } else {
                //EDIT CHILD
                fragmentScreenSwitcher.open(new AddChildFragment.Screen(childOdForEdit));
            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        activitySwitcher.detach(this);
        applicationSwitcher.detach(this);
    }

    public static final class Screen extends ActivityScreen {

        private int childIdForEdit;

        public Screen(int childIdForEdit) {
            this.childIdForEdit = childIdForEdit;
        }

        public Screen() {
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(AddChildFragment.Screen.EDIT_CHILD_ID, childIdForEdit);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return AddChildActivity.class;
        }
    }
}
