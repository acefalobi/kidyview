package com.ltst.core.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.ltst.core.base.CoreActivity;

/**
 * Class, that used for unification of switching activities in both applications (Kite and KiteAgent)
 * Wrapper for intents of activities, that used for transaction
 */
public abstract class ActivityScreen implements Screen {

    private static final String BF_TRANSITION_VIEW = "ActivityScreen.transitionView";

    /**
     * transitionView - need for ActivityOptionsCompat method makeSceneTransitionAnimation
     * can be used for create animation of switching of activities
     * @see  ActivityOptionsCompat
     */
    @Nullable
    private View transitionView;

    public void attachTransitionView(@Nullable View view) {
        transitionView = view;
    }

    @Nullable
    protected View detachTransitionView() {
        View view = transitionView;
        transitionView = null;
        return view;
    }

    /**
     *
     * @param context -context of existing activity
     * @return Intent for switch existing activity
     * @see ActivityScreenSwitcher method open(ActivityScreen screen)
     */
    @NonNull
    public final Intent intent(Context context) {
        Intent intent = new Intent(context, activityClass());
        configureIntent(intent);
        return intent;
    }

    protected final Bundle activityOptions(Activity activity) {
        View transitionView = detachTransitionView();
        if (transitionView == null) {
            return null;
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, BF_TRANSITION_VIEW).toBundle();
    }

    /**
     *
     * @param intent - intent of existing activity before switch
     */
    protected abstract void configureIntent(@NonNull Intent intent);

    /**
     *
     * @return instance of class of activity, that must be run
     */
    protected abstract Class<? extends CoreActivity> activityClass();

    public static void setTransitionView(View view) {
        ViewCompat.setTransitionName(view, BF_TRANSITION_VIEW);
    }

}
