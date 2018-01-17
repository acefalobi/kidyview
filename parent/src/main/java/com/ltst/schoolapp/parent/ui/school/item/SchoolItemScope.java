package com.ltst.schoolapp.parent.ui.school.item;


import android.os.Bundle;

import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.school.SchoolInfoWrapper;
import com.ltst.schoolapp.parent.ui.school.item.fragment.ItemScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface SchoolItemScope {

    @Module
    class SchoolItemModule {

        private final Bundle screenParams;

        public SchoolItemModule(Bundle screenParams) {
            this.screenParams = screenParams;
        }

        @Provides
        @SchoolItemScope
        SchoolInfoWrapper provideItem() {
            return ((SchoolInfoWrapper) screenParams.getParcelable(SchoolItemActivity.Screen.INFO_ITEM_KEY));
        }
    }

    @SchoolItemScope
    @Component(dependencies = ParentScope.ParentComponent.class, modules = SchoolItemScope.SchoolItemModule.class)
    interface SchoolItemComponent {

        void inject(SchoolItemActivity schoolItemActivity);

        ItemScope.ItemComponent subcomponent (ItemScope.ItemModule module);
    }
}
