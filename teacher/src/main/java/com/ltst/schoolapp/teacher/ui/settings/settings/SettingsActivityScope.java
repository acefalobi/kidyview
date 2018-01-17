package com.ltst.schoolapp.teacher.ui.settings.settings;

import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.settings.settings.fragment.SettingsScope;

import javax.inject.Scope;

import dagger.Component;

@Scope
public @interface SettingsActivityScope {

    @SettingsActivityScope
    @Component(dependencies = {TeacherComponent.class})
    interface SettingsActivityComponent {
        void inject(SettingsActivity activity);

        SettingsScope.SettingsComponent settingsComponent(SettingsScope.SettingsModule module);
    }
}
