package com.ltst.schoolapp.parent.ui.conversation;


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
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.conversation.fragment.ConversationFragment;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

import javax.inject.Inject;

public class ConversationActivity extends ParentActivity implements HasFragmentContainer,
        HasSubComponents<ConversationScope.ConversationComponent> {

    @Inject ActivityScreenSwitcher activitySwitcher;

    private ConversationScope.ConversationComponent component;
    private Bundle screenParams;
    private boolean openedFromNotification;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenParams = getIntent().getExtras(); //for opening screen from notification
        if (screenParams.containsKey(LayerNotificationsHelper.FROM_NOTIFICATION_KEY)) {
            openedFromNotification = screenParams.getBoolean(LayerNotificationsHelper.FROM_NOTIFICATION_KEY);
        }
    }

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;
        if (screenParams.containsKey(LayerNotificationsHelper.FROM_NOTIFICATION_KEY)) {
            openedFromNotification = screenParams.getBoolean(LayerNotificationsHelper.FROM_NOTIFICATION_KEY);
        }
    }

    @Override public void onBackPressed() {
        if (openedFromNotification) {
            activitySwitcher.open(new MainActivity.Screen());
        } else {
            super.onBackPressed();
        }
    }

    @Override protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerConversationScope_ConversationComponent.builder()
                .parentComponent(component)
                .conversationModule(new ConversationScope.ConversationModule(screenParams))
                .build();
        this.component.inject(this);
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

    @Override public ConversationScope.ConversationComponent getComponent() {
        return component;
    }

    @Override protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new ConversationFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activitySwitcher.detach(this);
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

        public Screen(String[] layerIds, String screenTitle) {
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
