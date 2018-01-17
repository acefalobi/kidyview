package com.ltst.schoolapp.parent.ui.enter.start;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface StartContract {

    interface Presenter extends BasePresenter {
        void goToLogin();

        void goToRegistration();
    }

    interface View extends BaseView<Presenter> {

    }
}
