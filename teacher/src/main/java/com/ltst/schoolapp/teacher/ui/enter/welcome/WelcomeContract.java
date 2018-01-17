package com.ltst.schoolapp.teacher.ui.enter.welcome;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface WelcomeContract {

    interface Presenter extends BasePresenter {

        void nextScreen();
    }

    interface View extends BaseView<Presenter> {

    }
}
