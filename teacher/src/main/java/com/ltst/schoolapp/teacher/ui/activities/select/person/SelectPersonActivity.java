package com.ltst.schoolapp.teacher.ui.activities.select.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.ui.activities.select.person.SelectPersonActivityScope.SelectPersonActivityModule;
import com.ltst.schoolapp.teacher.ui.activities.select.person.fragment.SelectPersonFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectPersonActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<SelectPersonActivityScope.SelectPersonActivityComponent> {

    @Inject
    ActivityScreenSwitcher screenSwitcher;

    private SelectPersonActivityScope.SelectPersonActivityComponent component;

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
        component = DaggerSelectPersonActivityScope_SelectPersonActivityComponent.builder()
                .teacherComponent(teacherComponent)
                .selectPersonActivityModule(new SelectPersonActivityModule(getIntent().getExtras()))
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new SelectPersonFragment.Screen());
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
    public SelectPersonActivityScope.SelectPersonActivityComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_SELECTED_PERSONS = "SelectPersonActivity.Screen.models";
        public static final String KEY_SELECTED_GROUP = "SelectPersonActivity.Screen.grouId";
        private final ArrayList<SelectPersonModel> models;
        private final long groupId;

        public Screen(long groupId, ArrayList<SelectPersonModel> models) {
            this.models = models;
            this.groupId = groupId;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            if (models != null) {
                intent.putExtra(KEY_SELECTED_PERSONS, models);
                intent.putExtra(KEY_SELECTED_GROUP, groupId);
            }
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return SelectPersonActivity.class;
        }
    }


}
