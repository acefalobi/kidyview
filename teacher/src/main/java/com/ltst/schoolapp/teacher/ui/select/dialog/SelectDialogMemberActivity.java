package com.ltst.schoolapp.teacher.ui.select.dialog;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.select.dialog.fragment.SelectMemberFragment;

import javax.inject.Inject;

public class SelectDialogMemberActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<SelectDialogMemberScope.SelectDialogMemberComponent> {

    @Inject ActivityScreenSwitcher activityScreenSwitcher;

    private SelectDialogMemberScope.SelectDialogMemberComponent component;
    private Bundle screenParams;

    @Override protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.default_toolbar));
    }

    @Override public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override public SelectDialogMemberScope.SelectDialogMemberComponent getComponent() {
        return component;
    }

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;

    }

    @Override protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        this.component = DaggerSelectDialogMemberScope_SelectDialogMemberComponent.builder()
                .teacherComponent(teacherComponent)
                .selectDialogMemberModule(new SelectDialogMemberScope.SelectDialogMemberModule(screenParams))
                .build();
        this.component.inject(this);
    }

    @Override protected void onStart() {
        super.onStart();
        activityScreenSwitcher.attach(this);
        if (getFragmentScreenSwitcher().getFragmentsCount() == 0) {
            getFragmentScreenSwitcher().open(new SelectMemberFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activityScreenSwitcher.detach(this);
    }

    public static final class Screen extends ActivityScreen {

        public static final String KEY_SCREEN_MODE = "SelectDialogMemberActivity.Screen.Mode";

        private final @ScreenMode int screenMode;

        public Screen(@ScreenMode int screenMode) {
            this.screenMode = screenMode;
        }

        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_SCREEN_MODE,screenMode);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return SelectDialogMemberActivity.class;
        }
    }

    public static final int START_SINGLE_CHAT = 1;
    public static final int START_GROUP_CHAT = 2;

    @IntDef({START_SINGLE_CHAT, START_GROUP_CHAT})
    public @interface ScreenMode {
    }
}
