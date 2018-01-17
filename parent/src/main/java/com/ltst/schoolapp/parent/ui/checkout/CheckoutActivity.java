package com.ltst.schoolapp.parent.ui.checkout;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.checkout.fragment.info.InfoFragment;

import javax.inject.Inject;

public class CheckoutActivity extends ParentActivity implements HasFragmentContainer, HasSubComponents<CheckoutScope.CheckoutComponent> {

    @Inject ActivityScreenSwitcher activitySwitcher;

    private CheckoutScope.CheckoutComponent component;
    
    @Override
    protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerCheckoutScope_CheckoutComponent.builder()
                .parentComponent(component)
                .checkoutModule(new CheckoutScope.CheckoutModule(getFragmentScreenSwitcher(),
                        getDialogProvider()))
                .build();
        this.component.inject(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.default_activity_green;
    }

    @Override
    protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.default_toolbar));
    }

    @Override
    protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        if (!getFragmentScreenSwitcher().hasFragments()) {
            getFragmentScreenSwitcher().open(new InfoFragment.Screen());
        }
    }

    @Override
    protected void onStop() {
        activitySwitcher.detach(this);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public CheckoutScope.CheckoutComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {

        @Override
        protected void configureIntent(@NonNull Intent intent) {

        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return CheckoutActivity.class;
        }
    }
}
