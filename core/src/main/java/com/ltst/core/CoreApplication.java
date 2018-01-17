package com.ltst.core;

import android.graphics.Typeface;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.ltst.core.util.FakeCrashLibrary;
import com.ltst.core.util.Foreground;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CoreApplication extends MultiDexApplication {

    private CoreComponent coreComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initDaggerDependencies();
        initTimber();
        initTypeFaces();
        initForeground();
    }

    private void initForeground() {
        Foreground.init(this);
    }

    private void initTypeFaces() {
        Typeface openSansLight = Typeface.createFromAsset(getAssets(), "fonts/open-sans.light.ttf");
        Typeface openSans = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface openSansExtraBolt = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-ExtraBold.ttf");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private void initDaggerDependencies() {
        coreComponent = DaggerCoreComponent.builder()
                .coreAppModule(new CoreAppModule(getApplicationContext()))
                .build();
    }

    public CoreComponent getCoreComponent() {
        return coreComponent;
    }


    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }
        }
    }
}
