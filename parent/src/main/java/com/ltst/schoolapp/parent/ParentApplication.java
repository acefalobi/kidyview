package com.ltst.schoolapp.parent;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.ltst.core.CoreApplication;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import io.fabric.sdk.android.Fabric;

public class ParentApplication extends CoreApplication {

    private ParentScope.ParentComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initAppDagger();
        initStetho();
    }

    private void initAppDagger() {
        component = DaggerParentScope_ParentComponent.builder()
                .coreComponent(getCoreComponent())
                .parentAppModule(new ParentScope.ParentAppModule(this))
                .build();
        component.inject(this);
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    public static ParentApplication get(Context context) {
        return (ParentApplication) context.getApplicationContext();
    }

    public ParentScope.ParentComponent getComponent() {
        return component;
    }
}


