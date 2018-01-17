package com.ltst.core.navigation;

/**
 * Base abstraction for ActivityScreenSwitcher and FragmentScreenSwitcher
 * @see ActivityScreenSwitcher
 * @see FragmentScreenSwitcher
 * @param <S>
 */
public interface ScreenSwitcher<S extends Screen> {
    void open(S screen);
    void goBack();
}
