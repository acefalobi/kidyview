package com.ltst.schoolapp.parent.ui.checkout;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.checkout.fragment.info.InfoScope;
import com.ltst.schoolapp.parent.ui.checkout.fragment.share.ShareCodeScope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckoutScope {

    @Module
    class CheckoutModule{
        private final FragmentScreenSwitcher fragmentScreenSwitcher;
        private final DialogProvider dialogProvider;

        public CheckoutModule(FragmentScreenSwitcher fragmentScreenSwitcher, DialogProvider dialogProvider) {
            this.fragmentScreenSwitcher = fragmentScreenSwitcher;
            this.dialogProvider = dialogProvider;
        }

        @CheckoutScope
        @Provides
        FragmentScreenSwitcher provideFragmentScreenSwitcher(){
            return this.fragmentScreenSwitcher;
        }

        @CheckoutScope
        @Provides
        DialogProvider provideDialogProvider(){
            return this.dialogProvider;
        }
    }

    @CheckoutScope
    @Component (dependencies = ParentScope.ParentComponent.class, modules = CheckoutModule.class)
    interface CheckoutComponent {

        void inject(CheckoutActivity checkoutActivity);

        InfoScope.InfoComponent infoComponent (InfoScope.InfoModule module);

        ShareCodeScope.ShareCodeComponent shareComponent (ShareCodeScope.ShareCodeModule module);
    }
}
