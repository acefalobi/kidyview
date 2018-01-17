package com.ltst.schoolapp.parent.ui.checkout.fragment.info;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface InfoScope {

    @Module
    class InfoModule{
        private final InfoContract.View view;

        public InfoModule(InfoContract.View view) {
            this.view = view;
        }

        @Provides
        @InfoScope
        InfoContract.View provideView(){
            return this.view;
        }
    }

    @Subcomponent(modules = InfoScope.InfoModule.class)
    @InfoScope
    interface InfoComponent {
        void inject (InfoFragment fragment);
    }
}
