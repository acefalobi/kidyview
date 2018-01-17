package com.ltst.schoolapp.teacher.ui.settings.changepassword;

import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment.ChangePasswordScope;
import com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment.ChangePasswordScope.ChangePasswordModule;

import javax.inject.Scope;

import dagger.Component;

@Scope
public @interface ChangePasswordActivityScope {

    @ChangePasswordActivityScope
    @Component(dependencies = {TeacherComponent.class})
    interface ChangePasswordActivityComponent {
        void inject(ChangePasswordActivity activity);

        ChangePasswordScope.ChangePasswordComponent changePassword(ChangePasswordModule module);

    }
}
