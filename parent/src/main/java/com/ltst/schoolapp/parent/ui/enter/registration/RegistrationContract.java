package com.ltst.schoolapp.parent.ui.enter.registration;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface RegistrationContract {

    interface Presenter extends BasePresenter {
        void login();

        void setEmail(String email);

        void setPassword(String password);

        void setConfirmPassword(String password);

        void setCode(String code);

        void goBack();

        void sendCodeAgain();
    }

    interface View extends BaseView<Presenter> {

        void startLoad();

        void stopLoad();

        void netError();

        void serverRegistrationError();

        void codeValidationError();

        void emailValidationError();

        void passwordConfirmError();

        void passwordValidationError();

        void sendAgainLoggedInUserError();

        void sendAgainSuccess();
    }
}
