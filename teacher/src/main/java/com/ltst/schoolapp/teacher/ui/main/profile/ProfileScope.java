package com.ltst.schoolapp.teacher.ui.main.profile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileScope {

    @ProfileScope
    @Subcomponent(modules = ProfileModule.class)
    interface ProfileComponent {

        void inject(ProfileFragment profileFragment);
    }

    @Module
    class ProfileModule {
        private final ProfileContract.View view;

        public ProfileModule(ProfileContract.View view) {
            this.view = view;
        }

        @Provides
        @ProfileScope
        ProfileContract.View provideProfileView() {
            return this.view;
        }
    }
}
