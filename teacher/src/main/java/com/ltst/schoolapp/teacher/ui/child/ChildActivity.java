package com.ltst.schoolapp.teacher.ui.child;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.Child;
import com.ltst.core.firebase.PushNotification;
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
import com.ltst.schoolapp.teacher.firebase.message.notification.ChangeChildNotification;
import com.ltst.schoolapp.teacher.firebase.message.notification.FamilyRequestNotification;
import com.ltst.schoolapp.teacher.ui.child.viewchild.ViewChildFragment;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<ChildScope.ChildComponent> {

    @BindView(R.id.default_toolbar) Toolbar toolbar;

    @Inject ActivityScreenSwitcher activityScreenSwitcher;

    @Inject ApplicationSwitcher applicationSwitcher;

    private ChildScope.ChildComponent component;

    private Child child;

    private boolean fromNotification;

    private long requestMemberId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override public void onBackPressed() {
        if (!fromNotification) {
            super.onBackPressed();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                activityScreenSwitcher.open(new MainActivity.Screen(BottomScreen.FEED));
            } else super.onBackPressed();

        }
    }

    @Override
    protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        if (params != null) { // from notification
            if (params.containsKey(PushNotification.KEY_FROM_PUSH)) {
                prepareDataFromNotification(params);
            } else {
                prepareDataFromBackScreen(params);
            }

        }

    }


    private void prepareDataFromNotification(Bundle params) {
        fromNotification = params.getBoolean(PushNotification.KEY_FROM_PUSH);
        this.child = new Child();
        long childServerId;
        if (params.containsKey(ChangeChildNotification.INTENT_KEY_CHILD_ID)) {
            childServerId = params.getLong(ChangeChildNotification.INTENT_KEY_CHILD_ID);
            child.setServerId(childServerId);
        } else if (params.containsKey(FamilyRequestNotification.INTENT_KEY_CHILD_ID)) {
            childServerId = params.getLong(FamilyRequestNotification.INTENT_KEY_CHILD_ID);
            child.setServerId(childServerId);
            if (params.containsKey(FamilyRequestNotification.INTENT_KEY_MEMBER_ID)) {
                requestMemberId = params.getLong(FamilyRequestNotification.INTENT_KEY_MEMBER_ID);
            }
        }


    }

    private void prepareDataFromBackScreen(Bundle params) {
        int childId = params.getInt(Screen.CHILD_SCREEN_ID);
        String firstName = params.getString(Screen.CHILD_FIRST_NAME);
        String lastName = params.getString(Screen.CHILD_LAST_NAME);
        child = new Child();
        child.setId(childId);
        child.setName(firstName);
        child.setLastName(lastName);
    }


    @Override
    protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerChildScope_ChildComponent.builder()
                .teacherComponent(teacherComponent)
                .childModule(new ChildScope.ChildModule(
                        this.child,
                        requestMemberId,
                        getFragmentScreenSwitcher(),
                        getActivityProvider(),
                        getDialogProvider(),
                        new GalleryPictureLoader(this)))
                .build();
        component.inject(this);
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
    public ChildScope.ChildComponent getComponent() {
        return component;
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (child.getFirstName() != null && child.getLastName() != null) {
            toolbar.setTitle(child.getFirstName() + StringUtils.SPACE + child.getLastName());
        }
        activityScreenSwitcher.attach(this);
        applicationSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new ViewChildFragment.Screen());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        activityScreenSwitcher.detach(this);
        applicationSwitcher.detach(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class Screen extends ActivityScreen {
        public static final String CHILD_SCREEN_ID = "ChildActivity_Screen.ChildId";
        public static final String CHILD_FIRST_NAME = "ChildActivity_Screen.ChildFirstName";
        public static final String CHILD_LAST_NAME = "ChildActivity_Screen.ChildLastName";

        private final int childId;
        private final String firstName;
        private final String lastName;

        public Screen(int childId, String firstName, String lastName) {
            this.childId = childId;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(CHILD_SCREEN_ID, childId);
            intent.putExtra(CHILD_FIRST_NAME, firstName);
            intent.putExtra(CHILD_LAST_NAME, lastName);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return ChildActivity.class;
        }
    }
}
