package com.livetyping.utils.utils.keyboard;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class Keyboards {
    private Keyboards() {
        throw new AssertionError("No instances.");
    }

    public static void showKeyboard(View view) {
        getInputManager(view.getContext()).showSoftInput(view, 0);
    }

    private static InputMethodManager getInputManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
