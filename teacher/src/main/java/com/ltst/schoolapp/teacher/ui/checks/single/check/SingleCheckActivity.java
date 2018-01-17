package com.ltst.schoolapp.teacher.ui.checks.single.check;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.checks.single.check.SingleCheckActivityScope.SingleCheckActivityModule;
import com.ltst.schoolapp.teacher.ui.checks.single.check.fragment.SingleCheckFragment;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleCheckActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<SingleCheckActivityScope.SingleCheckActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private SingleCheckActivityScope.SingleCheckActivityComponent component;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    @Override
    protected void onExtractParams(Bundle params) {
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
        component = DaggerSingleCheckActivityScope_SingleCheckActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .singleCheckActivityModule(new SingleCheckActivityModule(getIntent().getExtras()))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new SingleCheckFragment.Screen());
        }
        screenSwitcher.attach(this);
    }

    @Override
    protected void onStop() {
        screenSwitcher.detach(this);
        super.onStop();
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public SingleCheckActivityScope.SingleCheckActivityComponent getComponent() {
        return component;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        screenSwitcher.open(new MainActivity.Screen(BottomScreen.CHECKS));
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_CHILDREN_CHECKS = "SingleCheckActivity.Screen.childrenChecks";
        private final ArrayList<ChildCheck> childrenChecks;

        public Screen(ArrayList<ChildCheck> childrenChecks) {
            this.childrenChecks = childrenChecks;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            if (childrenChecks != null) {
                intent.putExtra(KEY_CHILDREN_CHECKS, childrenChecks);
            }
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return SingleCheckActivity.class;
        }
    }


}
