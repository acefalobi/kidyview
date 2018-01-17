package com.ltst.schoolapp.parent.ui.checkout.fragment.share;

import android.os.Bundle;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
public @interface ShareCodeScope {

    @Module
    class ShareCodeModule {

        private final ShareCodeContract.View view;
        private final Bundle screenParams;

        public ShareCodeModule(ShareCodeContract.View view, Bundle screenParams) {
            this.view = view;
            this.screenParams = screenParams;
        }

        @ShareCodeScope
        @Provides
        ShareCodeContract.View provideView (){
            return view;
        }

        @ShareCodeScope
        @Provides
        Bundle provideScreenParams(){
            return this.screenParams;
        }
    }

    @ShareCodeScope
    @Subcomponent(modules = ShareCodeModule.class)
    interface ShareCodeComponent {

        void inject(ShareCodeFragment shareCodeFragment);
    }
    
}
