package com.ltst.schoolapp.teacher.ui.child.viewchild;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewChildScope {

    @ViewChildScope
    @Subcomponent(modules = ViewChildModule.class)
    interface ViewChildComponent {
        void inject(ViewChildFragment fragment);
    }

    @Module
    class ViewChildModule {
        private final ViewChildContract.View view;

        public ViewChildModule(ViewChildContract.View view) {
            this.view = view;
        }

        @Provides
        @ViewChildScope
        ViewChildContract.View provideViewChildView() {
            return this.view;
        }
    }
}
