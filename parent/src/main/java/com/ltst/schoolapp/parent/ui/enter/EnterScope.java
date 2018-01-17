package com.ltst.schoolapp.parent.ui.enter;

import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.enter.forgot.ForgotScope;
import com.ltst.schoolapp.parent.ui.enter.login.LoginScope;
import com.ltst.schoolapp.parent.ui.enter.newpass.NewPassScope;
import com.ltst.schoolapp.parent.ui.enter.registration.RegistrationScope;
import com.ltst.schoolapp.parent.ui.enter.start.StartScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface EnterScope {


    @EnterScope
    @dagger.Component(dependencies = ParentScope.ParentComponent.class, modules = EnterModule.class)
    interface EnterComponent {
        void inject(EnterActivity activity);

        StartScope.StartComponent startComponent(StartScope.StartModule module);

        LoginScope.LoginComponent loginComponent (LoginScope.LoginModule module);

        RegistrationScope.Component registrationComponent (RegistrationScope.Module module);

        ForgotScope.ForgotComponent forgotComponent(ForgotScope.ForgotModule forgotModule);

       NewPassScope.NewPassComponent newPassComponent(NewPassScope.NewPassModule newPassModule);
    }

    @dagger.Module
    class EnterModule {
        private final FragmentScreenSwitcher fragmentSwitcher;
        private final DialogProvider dialogProvider;

        public EnterModule(FragmentScreenSwitcher fragmentSwitcher, DialogProvider dialogProvider) {
            this.fragmentSwitcher = fragmentSwitcher;
            this.dialogProvider = dialogProvider;
        }

        @Provides
        @EnterScope
        FragmentScreenSwitcher provideFragmentScreenSwitcher() {
            return this.fragmentSwitcher;
        }

        @Provides
        @EnterScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }

        @Provides
        @EnterScope
        Profile provideEnterProfile(){
            return new Profile();
        }
    }
}
