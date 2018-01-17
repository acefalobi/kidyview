package com.ltst.schoolapp.parent.ui.child.view;

import com.ltst.schoolapp.parent.ui.child.edit.fragment.EditChildFragmentScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewChildScope {

    @Module
    class ViewChildModule {
        private final ViewChildContract.View view;

        public ViewChildModule(ViewChildContract.View view) {
            this.view = view;
        }

        @Provides
        @ViewChildScope
        ViewChildContract.View provideView() {
            return this.view;
        }
    }

    @ViewChildScope
    @Subcomponent (modules = ViewChildScope.ViewChildModule.class)
    interface ViewChildComponent {

        void inject(ViewChildFragment viewChildFragment);

        EditChildFragmentScope.EditChildFragmentComponent editChildComponent
                (EditChildFragmentScope.EditChildFragmentModule module);
    }
}
