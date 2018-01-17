package com.ltst.schoolapp.teacher.ui.child.checkemail;


import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface CheckEmailContract {

    interface Presenter extends BasePresenter {
        void goBack();

        void setEmail(String string);

        void done();
    }

    interface View extends BaseView<Presenter> {

        void enableCheckButton(boolean enable);

        void networkError();

        void stopLoad();

        void startLoad();

        void bindName(String name, String lastName);

        void existEmailError();
    }
}
