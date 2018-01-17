package com.ltst.schoolapp.teacher.ui.school;


import android.os.Bundle;

import com.ltst.core.data.model.Profile;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.editprofile.EditProfileActivity;
import com.ltst.schoolapp.teacher.ui.school.fragment.SchoolFragmentScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

//import com.ltst.schoolapp.teacher.ui.school.fragment.SchoolFragmentScope;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SchoolScope {

    @Module
    class SchoolModule {
        private Profile profile;

        SchoolModule(Bundle screenParams) {
            this.profile = screenParams.getParcelable(EditProfileActivity.Screen.KEY_PROFILE);
        }

        @Provides
        @SchoolScope
        Profile provideProfile() {
            return profile;
        }
    }

    @SchoolScope
    @Component(dependencies = TeacherComponent.class, modules = SchoolScope.SchoolModule.class)
    interface SchoolComponent {
        void inject(SchoolActivity activity);

        SchoolFragmentScope.SchoolFragmentComponent schoolFragmentComponent(SchoolFragmentScope.SchoolFragmentModule module);
    }
}
