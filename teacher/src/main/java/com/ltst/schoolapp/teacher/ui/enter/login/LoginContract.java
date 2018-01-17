package com.ltst.schoolapp.teacher.ui.enter.login;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.util.validator.ValidateType;

import java.util.Map;

public interface LoginContract {

    interface Presenter extends BasePresenter {

        void validate(Map<ValidateType, String> needValidate, String password);

        void forgotPassword();

    }

    interface View extends BaseView<Presenter> {

        void errorEmail();

        void setEmptyPasswordError();

        void loginError();

        void userNotFoundError();

        void stopLoad();

        void startLoad();

        void showNetError();
    }
}
