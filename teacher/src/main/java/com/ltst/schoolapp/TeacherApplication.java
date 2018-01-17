package com.ltst.schoolapp;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.layer.sdk.LayerClient;
import com.ltst.core.CoreApplication;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.fabric.sdk.android.Fabric;

public class TeacherApplication extends CoreApplication {

    private TeacherComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initTeacherDagger();
        initStetho();
        initLayer();
    }

    private void initLayer() {
        LayerClient.applicationCreated(this);
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    private void initTeacherDagger() {
        component = DaggerTeacherComponent.builder()
                .coreComponent(getCoreComponent())
                .teacherAppModule(new TeacherAppModule(this))
                .build();
        component.inject(this);
    }

    public static TeacherApplication get(Context context) {
        return (TeacherApplication) context.getApplicationContext();
    }

    public TeacherComponent getTeacherComponent() {
        return this.component;
    }

}
