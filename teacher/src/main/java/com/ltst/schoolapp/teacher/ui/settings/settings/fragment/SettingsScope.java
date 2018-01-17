package com.ltst.schoolapp.teacher.ui.settings.settings.fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SettingsScope {

    @SettingsScope
    @Subcomponent(modules = SettingsModule.class)
    interface SettingsComponent {
        void inject(SettingsFragment settingsFragment);
    }

    @Module
    class SettingsModule {
        private final SettingsContract.View view;

        public SettingsModule(SettingsContract.View view) {
            this.view = view;
        }

        @Provides
        @SettingsScope
        SettingsContract.View provideEditProfileView() {
            return this.view;
        }
    }
}
