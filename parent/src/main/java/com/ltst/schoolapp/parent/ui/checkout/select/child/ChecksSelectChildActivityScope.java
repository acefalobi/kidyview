package com.ltst.schoolapp.parent.ui.checkout.select.child;

import android.os.Bundle;

import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.checkout.select.child.fragment.ChecksSelectChildScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface ChecksSelectChildActivityScope {

    @ChecksSelectChildActivityScope
    @Component(dependencies = {ParentScope.ParentComponent.class}, modules = {ChecksSelectChildActivityModule.class})
    interface ChecksSelectChildActivityComponent {
        void inject(ChecksSelectChildActivity activity);

        ChecksSelectChildScope.ChecksSelectChildComponent
        checksSelectChildComponent(ChecksSelectChildScope.ChecksSelectChildModule module);

    }

    @Module
    class ChecksSelectChildActivityModule {
        private Bundle activityParams;

        public ChecksSelectChildActivityModule(Bundle activityParams) {
            this.activityParams = activityParams;
        }

        @Provides
        Bundle provideActivityParams() {
            return activityParams;
        }
    }
}
