package com.ltst.core.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Wrapper for Fragment for switch between Fragments
 */
public abstract class FragmentScreen implements Screen {

    public static final String BF_NAME = "FragmentScreen.name";

    @Nullable private Fragment fragment;

    /**
     *
     * @return instance of Fragment, used in
     * @see FragmentScreenSwitcher
     */
    @Nullable
    public Fragment getFragment() {
        if (fragment != null) {
            return fragment;
        }
        fragment = createFragment();
        if (fragment == null) {
            throw new IllegalStateException("createFragment() returns null");
        }
        Bundle arguments = new Bundle();
        onAddArguments(arguments);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * used for transfer the data between fragments
     * @param arguments
     */
    protected void onAddArguments(Bundle arguments) {
        arguments.putString(BF_NAME, getName());
    }

    /**
     *
     * @return tag, used for tags in
     * @see android.support.v4.app.FragmentManager
     */
    public abstract String getName();

    /**
     * used for creante new instance of Fragments in all of children of FragmentScreen
     * @return new Instance of fragment
     */
    protected abstract Fragment createFragment();
}
