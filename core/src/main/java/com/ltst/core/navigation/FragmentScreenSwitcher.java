package com.ltst.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.livetyping.utils.utils.StringUtils;

import java.security.InvalidParameterException;

/**
 * class used for swith between fragments
 */
public class FragmentScreenSwitcher implements ScreenSwitcher<FragmentScreen>/*,
        FragmentManager.OnBackStackChangedListener*/ {


    private AppCompatActivity mActivity;

    private FragmentManager mFragmentManager;

    @Nullable
    private String mResultFragmentTag;

    @Nullable
    private Integer mRequestCode;

    /**
     * @param activity - activity, which implement HasFragmentContainer
     * @see HasFragmentContainer
     */
    public void attach(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    /**
     * @return has FragmentScreenSwitcher added fragments in FragmentManager ar no
     */
    public boolean hasFragments() {
        return getFragmentsCount() > 0;
    }

    public int getFragmentsCount() {
        int fragmentSize = 0;
        if (getSupportFragmentManager() == null || getSupportFragmentManager().getFragments() == null)
            return 0;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragmentSize++;
            }
        }
        boolean hasGlideFragment = getSupportFragmentManager().findFragmentByTag(
                "com.bumptech.glide" + ".manager") != null;
        return fragmentSize - (hasGlideFragment ? 1 : 0);
    }

    public boolean canActivityGoBack() {
        return getSupportFragmentManager().getBackStackEntryCount() < 1;
    }


    public void open(FragmentScreen screen, boolean addToBackSack) {
        mResultFragmentTag = screen.getName();
        if (mActivity instanceof HasFragmentContainer) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            int containerId = ((HasFragmentContainer) mActivity).getFragmentContainerId();
            String name = screen.getName();
            fragmentTransaction.replace(containerId, screen.getFragment(), name);
            if (addToBackSack) {
                fragmentTransaction.addToBackStack(name);
            }
            fragmentTransaction.commit();
        } else {
            throw new InvalidParameterException(getException(mActivity));
        }
    }

    /**
     * used for open new Fragment screen
     *
     * @param screen - instance of FragmentScreen
     * @see FragmentScreen
     */
    @Override
    public void open(FragmentScreen screen) {
        open(screen, true);
    }

    public void openWithClearStack(FragmentScreen fragmentScreen) {
        getSupportFragmentManager().popBackStack();
        open(fragmentScreen);
    }


    public void openWithClearStack(String name) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * back in back stack of fragments
     */
    @Override
    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }

    public void detach() {
        mActivity = null;
        mFragmentManager = null;
    }

    /**
     * usud for get result from fragment, which was started with method startForResult
     *
     * @param intent - Intent with data, intent is transmitted in onActivityResult of fragment
     */
    public void onFragmentResult(Intent intent) {
        goBack();
        if (!StringUtils.isBlank(mResultFragmentTag)) {
            Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(mResultFragmentTag);
            fragmentByTag.onActivityResult(mRequestCode, Activity.RESULT_OK, intent);
            mRequestCode = null;

        }
    }

    public FragmentManager getSupportFragmentManager() {
        if (mFragmentManager != null) {
            return mFragmentManager;
        } else {
            if (mActivity == null) {
                return null;
            }
            mFragmentManager = mActivity.getSupportFragmentManager();
            return mFragmentManager;
        }
    }

    public android.app.FragmentManager getFragmentManager() {
        return mActivity.getFragmentManager();
    }


    private String getException(AppCompatActivity mActivity) {
        String className = mActivity.getClass().getName();
        String ex = String.format("class %s must implements HasFragmentContainer interface", className);
        return ex;
    }

    public void showDialogFragment(FragmentScreen fragmentScreen) {
        Fragment fragment = fragmentScreen.getFragment();
        if (fragment instanceof DialogFragment) {
            ((DialogFragment) fragment).show(getSupportFragmentManager(), fragmentScreen.getName());
        }
    }

}

