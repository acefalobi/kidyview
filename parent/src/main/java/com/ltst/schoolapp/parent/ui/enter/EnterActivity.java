package com.ltst.schoolapp.parent.ui.enter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.ParentActivity;
import com.ltst.schoolapp.parent.ui.enter.start.StartFragment;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class EnterActivity extends ParentActivity
        implements HasSubComponents<EnterScope.EnterComponent>, HasFragmentContainer {

    private EnterScope.EnterComponent component;

    @Inject ActivityScreenSwitcher activitySwitcher;

    @Inject DataService dataService;

    private Subscription enterSubscription;

    @Override
    protected void addToParentComponent(ParentScope.ParentComponent component) {
        this.component = DaggerEnterScope_EnterComponent.builder()
                .parentComponent(component)
                .enterModule(new EnterScope.EnterModule(
                        getFragmentScreenSwitcher(),
                        getDialogProvider()))
                .build();
        this.component.inject(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_enter;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    public EnterScope.EnterComponent getComponent() {
        return component;
    }

    @Override
    protected void onStart() {
        super.onStart();
        activitySwitcher.attach(this);
        enterSubscription = dataService.onceOlnydeauthenticate()
                .flatMap(aBoolean -> dataService.getServerToken())
                .subscribe(enterAction);

    }

    private final Action1<String> enterAction = serverToken -> {
        if (StringUtils.isBlank(serverToken)) {
            FragmentScreenSwitcher fragmentScreenSwitcher1 = getFragmentScreenSwitcher();
            if (!fragmentScreenSwitcher1.hasFragments()) {
                fragmentScreenSwitcher1.open(new StartFragment.Screen());
            }
        } else {
            activitySwitcher.open(new MainActivity.Screen());
        }
    };

    @Override
    protected void onStop() {
        activitySwitcher.detach(this);
        enterSubscription.unsubscribe();
        super.onStop();
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.enter_fragment_container;
    }

    public static final class Screen extends ActivityScreen {

        @Override protected void configureIntent(@NonNull Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        @Override protected Class<? extends CoreActivity> activityClass() {
            return EnterActivity.class;
        }
    }

}
