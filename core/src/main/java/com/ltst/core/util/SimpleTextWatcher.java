package com.ltst.core.util;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {

    private final SimpleTextChangeListener textChangeListener;

    public SimpleTextWatcher(SimpleTextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (textChangeListener != null) {
            textChangeListener.textChange(s);
        }
    }

    public interface SimpleTextChangeListener {
        void textChange(Editable s);
    }
}
