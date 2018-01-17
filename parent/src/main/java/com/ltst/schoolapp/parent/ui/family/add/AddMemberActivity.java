package com.ltst.schoolapp.parent.ui.family.add;

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
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.family.add.check.CheckEmailFragment;

import java.util.ArrayList;

import javax.inject.Inject;

public class AddMemberActivity extends ParentActivity
        implements HasSubComponents<AddMemberScope.AddMemberComponent>,
        HasFragmentContainer {

    @Inject ActivityScreenSwitcher activityScreenSwitcher;
    @Inject ApplicationSwitcher applicationSwitcher;

    private AddMemberScope.AddMemberComponent component;
    private Bundle screenParams;

    @Override
    protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        this.screenParams = params;
    }

    @Override
    protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerAddMemberScope_AddMemberComponent.builder()
                .parentComponent(component)
                .addMemberModule(new AddMemberScope.AddMemberModule(screenParams,
                        getActivityProvider(),
                        new GalleryPictureLoader(this),
                        getDialogProvider(),
                        getFragmentScreenSwitcher()))
                .build();
        this.component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityScreenSwitcher.attach(this);
        activityScreenSwitcher.attach(this);
        FragmentScreenSwitcher fragmentScreenSwitcher = getFragmentScreenSwitcher();
        if (!fragmentScreenSwitcher.hasFragments()) {
            fragmentScreenSwitcher.open(new CheckEmailFragment.Screen());
        }
    }

    @Override
    protected void onStop() {
        activityScreenSwitcher.detach(this);
        applicationSwitcher.detach(this);
        super.onStop();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override
    protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.default_toolbar));
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public AddMemberScope.AddMemberComponent getComponent() {
        return component;
    }

    public static final class Screen extends ActivityScreen {

        public static final int RESULT_FAMILY_MEMBER_REQUEST_CODE = 5432;
        public static final String RESULT_FAMILY_MEMBER_KEY = "AddMemberActivity.CreatedMember";

        public static final String CHILD_ID_KEY = "AddMemberActivity.ChildId";
        public static final String SCHOOL_ID_KEY = "AddMemberActivity.SchoolId";
        public static final String CHILD_FIRST_NAME_KEY = "AddMemberActivity.FirstName";
        public static final String CHILD_LAST_NAME_KEY = "AddMemberActivity.LastName";
        public static final String CHILD_EXISTING_PARENTS = "AddMemberActivity.Parents";


        private final long childId;
        private final long schoolId;
        private final String childFirstName;
        private final String childLastName;
        private final ArrayList<String> existingChildParents;

        public Screen(long childId, long schoolId, String childFirstName, String childLastName,
                      ArrayList<String> existingChildParents) {
            this.childId = childId;
            this.schoolId = schoolId;
            this.childFirstName = childFirstName;
            this.childLastName = childLastName;
            this.existingChildParents = existingChildParents;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.putExtra(CHILD_ID_KEY, childId);
            intent.putExtra(SCHOOL_ID_KEY, schoolId);
            intent.putExtra(CHILD_FIRST_NAME_KEY, childFirstName);
            intent.putExtra(CHILD_LAST_NAME_KEY, childLastName);
            intent.putStringArrayListExtra(CHILD_EXISTING_PARENTS, existingChildParents);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return AddMemberActivity.class;
        }


    }
}
