package com.ltst.schoolapp.teacher.ui.activities.select.person;

import android.os.Bundle;

import com.ltst.schoolapp.TeacherComponent;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static com.ltst.schoolapp.teacher.ui.activities.select.person.fragment.SelectPersonScope.SelectPersonComponent;
import static com.ltst.schoolapp.teacher.ui.activities.select.person.fragment.SelectPersonScope.SelectPersonModule;

@Scope
public @interface SelectPersonActivityScope {

    @SelectPersonActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {SelectPersonActivityModule.class})
    interface SelectPersonActivityComponent {
        void inject(SelectPersonActivity activity);

        SelectPersonComponent selectPersonComponent(SelectPersonModule module);

    }

    @Module
    public class SelectPersonActivityModule {
        private Bundle activityParams;

        public SelectPersonActivityModule(Bundle activityParams) {
            this.activityParams = activityParams;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }
    }
}
