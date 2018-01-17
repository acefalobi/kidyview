package com.ltst.schoolapp.parent.ui.report;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.Post;
import com.ltst.core.firebase.PushNotification;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.main.MainActivity;
import com.ltst.schoolapp.parent.ui.report.fragment.ReportFragment;

import javax.inject.Inject;

public class ReportActivity extends ParentActivity implements HasSubComponents<ReportScope.ReportComponent>,
        HasFragmentContainer {

    private ReportScope.ReportComponent component;
    private Bundle screenParams;
    private boolean fromNotification;

    @Inject ActivityScreenSwitcher activitySwitcher;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(PushNotification.KEY_FROM_PUSH)) {
                fromNotification = extras.getBoolean(PushNotification.KEY_FROM_PUSH);
                this.screenParams = extras;
            }
        }
    }

    @Override public void onBackPressed() {
        if (!fromNotification) {
            super.onBackPressed();
        } else {
            activitySwitcher.open(new MainActivity.Screen());
        }

    }

    @Override protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerReportScope_ReportComponent.builder()
                .parentComponent(component)
                .reportModule(new ReportScope.ReportModule(screenParams, getFragmentScreenSwitcher()))
                .build();
        this.component.inject(this);

    }

    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;

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

    @Override public ReportScope.ReportComponent getComponent() {
        return component;
    }

    @Override protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new ReportFragment.Screen());
        }
    }

    @Override protected void onStop() {
        super.onStop();
        activitySwitcher.detach(this);
    }

    public static final class Screen extends ActivityScreen {

        public static final String KEY_REPORT_POST = "ReportActivity.Screen.report";

        private final Post reportPost;

        public Screen(Post reportPost) {
            this.reportPost = reportPost;
        }


        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(KEY_REPORT_POST, reportPost);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return ReportActivity.class;
        }
    }
}
