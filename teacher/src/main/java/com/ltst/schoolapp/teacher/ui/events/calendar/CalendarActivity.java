package com.ltst.schoolapp.teacher.ui.events.calendar;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.firebase.PushNotification;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.events.calendar.fragment.EventsFragment;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class CalendarActivity extends TeacherActivity implements HasSubComponents<CalendarScope.CalendarComponent>, HasFragmentContainer {

    private CalendarScope.CalendarComponent component;

    @Inject ActivityScreenSwitcher activityScreenSwitcher;
    private boolean openedFromNotification = false;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(PushNotification.KEY_FROM_PUSH)) {
                openedFromNotification = extras.getBoolean(PushNotification.KEY_FROM_PUSH);
            }
        }

    }

    @Override public void onBackPressed() {
        if (!openedFromNotification) {
            super.onBackPressed();
        } else {
            activityScreenSwitcher.open(new MainActivity.Screen());
        }
    }

    @Override protected void onStart() {
        super.onStart();
        activityScreenSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new EventsFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activityScreenSwitcher.detach(this);
    }

    @Override protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerCalendarScope_CalendarComponent.builder()
                .calendarModule(new CalendarScope.CalendarModule(getDialogProvider(),
                        new GalleryPictureLoader(this),
                        getFragmentScreenSwitcher(), getActivityProvider()))
                .teacherComponent(teacherComponent)
                .build();
        component.inject(this);
    }

    @Override protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override protected Toolbar getToolbar() {
        return ButterKnife.findById(this, R.id.default_toolbar);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override public CalendarScope.CalendarComponent getComponent() {
        return component;
    }

    @Override public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    public static final class Screen extends ActivityScreen {

        @Override protected void configureIntent(@NonNull Intent intent) {

        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return CalendarActivity.class;
        }
    }
}
