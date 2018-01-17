package com.ltst.schoolapp.teacher.ui.enter.newpass;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface NewPassContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void setCode(String code);

        void setPassword(String password);

        void checkData();
    }

    interface View extends BaseView<Presenter> {

        void validatePasswordError();

        void startLoad();

        void stopLoad();

        void showNetworkError();

        void setEmail(String email);

        void noEmailError();

        void wrongNumberError();
    }
}
