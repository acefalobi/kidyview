package com.ltst.schoolapp.parent.ui.select.dialog;


import android.os.Bundle;

import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.select.dialog.fragment.SelectMemberScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface SelectDialogMemberScope {

    @Module
    class SelectDialogMemberModule {
        private final Bundle screenParams;

        public SelectDialogMemberModule(Bundle screenParams) {
            this.screenParams = screenParams;
        }

        @Provides int provideScreenMode() {
            return this.screenParams.getInt(SelectDialogMemberActivity.Screen.KEY_SCREEN_MODE);
        }
    }

    @SelectDialogMemberScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = SelectDialogMemberScope.SelectDialogMemberModule.class)
    interface SelectDialogMemberComponent {

        void inject(SelectDialogMemberActivity selectDialogMemberActivity);

        SelectMemberScope.SelectMemberComponent selectMemeberComponent(SelectMemberScope.SelectMemberModule module);
    }
}
