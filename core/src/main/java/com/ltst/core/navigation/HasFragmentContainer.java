package com.ltst.core.navigation;

import android.support.annotation.IdRes;

/**
 * Abstraction for all activities, which has switching of fragments
 */
public interface HasFragmentContainer {

    /**
     * @return id of fragment`s container
     */
    @IdRes
    int getFragmentContainerId();


}
