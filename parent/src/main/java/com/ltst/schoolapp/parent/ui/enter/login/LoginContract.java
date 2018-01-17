package com.ltst.schoolapp.parent.ui.enter.login;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface LoginContract {

    interface Presenter extends BasePresenter {

        void done();

        void setEmail(String email);

        void setPassword(String password);

        void forgotPassword();
    }

    interface View extends BaseView<Presenter> {
        void emailValidationError();

        void emptyPassword();

        void startLoad();

        void stopLoad();

        void netError();

        void serverLoginError();
    }
}
