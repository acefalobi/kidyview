package com.ltst.schoolapp.teacher.ui.enter.start;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface StartContract {

    interface Presenter extends BasePresenter {
        void openCodeScreen();

        void openRegistrationScreen();

        void openLoginScreen();
    }

    interface View extends BaseView<Presenter> {

        void goToCodeScreen();

        void goToRegistrationScreen();

        void goToLoginScreen();

        void showLogoutFromServerPopup();
    }


}
