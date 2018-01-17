package com.livetyping.utils.managers.keyboard;


import android.app.Activity;
import android.view.View;

import com.livetyping.utils.Binder;
import com.livetyping.utils.utils.keyboard.KeyboardUtils;

public class KeyboardManager extends Binder<Activity> {

    public KeyboardManager() {
    }

    public void hide() {
        Activity activity = getAttachedObject();
        if (activity == null) return;
        KeyboardUtils.hideSoftKeyboard(activity);
    }

    public void hide(View view) {
        Activity activity = getAttachedObject();
        if (activity == null) return;
        KeyboardUtils.hideSoftKeyboard(view);
    }

    public void show(View view) {
        KeyboardUtils.showSoftKeyboard(view);
    }
}
