package com.ltst.schoolapp.teacher.ui.conversation;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.layer.LayerModule;
import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.conversation.fragment.ConversationFragment;

import javax.inject.Inject;

public class ConversationActivity extends TeacherActivity implements HasSubComponents<ConversationScope.ConversationComponent>,
        HasFragmentContainer {

    @Inject ActivityScreenSwitcher activityScreenSwitcher;
    private Bundle screenParams;

    private ConversationScope.ConversationComponent component;
    private boolean openedFromNotification;

    @Override protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.default_toolbar));
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.screenParams = getIntent().getExtras(); //for opening from notification
        screenParams = getIntent().getExtras(); //for opening screen from notification
        if (screenParams.containsKey(LayerNotificationsHelper.FROM_NOTIFICATION_KEY)) {
            openedFromNotification = screenParams.getBoolean(LayerNotificationsHelper.FROM_NOTIFICATION_KEY);
        }
    }

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;
        screenParams = getIntent().getExtras(); //for opening screen from notification
        if (screenParams.containsKey(LayerNotificationsHelper.FROM_NOTIFICATION_KEY)) {
            openedFromNotification = screenParams.getBoolean(LayerNotificationsHelper.FROM_NOTIFICATION_KEY);
        }
    }

    @Override public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override public ConversationScope.ConversationComponent getComponent() {
        return component;
    }

    @Override protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        component = DaggerConversationScope_ConversationComponent.builder()
                .teacherComponent(teacherComponent)
                .conversationModule(new ConversationScope.ConversationModule(screenParams))
                .build();
        component.inject(this);
    }

    @Override protected void onStart() {
        super.onStart();
        activityScreenSwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new ConversationFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activityScreenSwitcher.detach(this);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

    }

    public static final class Screen extends ActivityScreen {

        private final String[] layerIds;
        private final String screenTitle;

        public Screen(String[] layerIds, @NonNull String screenTitle) {
            this.layerIds = layerIds;
            this.screenTitle = screenTitle;
        }

        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(LayerModule.LAYER_IDENTITIES_KEY, layerIds);
            intent.putExtra(LayerModule.LAYER_SCREEN_TITLE_KEY, screenTitle);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return ConversationActivity.class;
        }
    }
}
