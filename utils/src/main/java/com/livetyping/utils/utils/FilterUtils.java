package com.livetyping.utils.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

public class FilterUtils {
    public static final InputFilter BLANK_SPACE_FILTER = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int i, int i1, Spanned spanned, int i2, int i3) {
            return source.toString().trim();
        }
    };
    public static final InputFilter LATIN_FILTER = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int i, int i1, Spanned spanned, int i2, int i3) {
            return source.toString().replaceAll("[^а-яА-Я0-9]+", StringUtils.EMPTY);

        }
    };

    public static void setFilters(EditText editText, InputFilter... filters) {
        editText.setFilters(filters);
    }
}
