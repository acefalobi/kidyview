package com.ltst.schoolapp.parent.ui.enter.forgot;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ForgotContract {

    interface Presenter extends BasePresenter {

        void done();

        void goBack();

        void setEmail(String text);
    }

    interface View extends BaseView <Presenter>{

        void emailRegexError();

        void emailNotExist();

        void stopLoad();

        void startLoad();

        void showNetworkError();
    }
}
