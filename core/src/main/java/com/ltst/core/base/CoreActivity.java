package com.ltst.core.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public abstract class CoreActivity extends AppCompatActivity {

    private FragmentScreenSwitcher fragmentScreenSwitcher;
    private DialogProvider dialogProvider;
    private ActivityProvider activityProvider;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            onExtractParams(params);
        }
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fragmentScreenSwitcher = new FragmentScreenSwitcher();
        dialogProvider = new DialogProvider(this);
        activityProvider = new ActivityProvider();
        setContentView(getLayoutResId());
        onCreateComponent();
    }



    protected void onExtractParams(Bundle params) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityProvider.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentScreenSwitcher.attach(this);
        activityProvider.attach(this);

    }

    @Override
    protected void onStop() {
        fragmentScreenSwitcher.detach();
        activityProvider.detach(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (fragmentScreenSwitcher.getFragmentsCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public FragmentScreenSwitcher getFragmentScreenSwitcher() {
        return fragmentScreenSwitcher;
    }

    public DialogProvider getDialogProvider() {
        return dialogProvider;
    }

    protected ActivityProvider getActivityProvider() {
        return activityProvider;
    }

    protected abstract int getLayoutResId();

    protected abstract void onCreateComponent();

    protected abstract Toolbar getToolbar();


}
