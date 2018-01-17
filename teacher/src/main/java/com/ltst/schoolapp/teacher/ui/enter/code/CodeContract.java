package com.ltst.schoolapp.teacher.ui.enter.code;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.util.validator.ValidateType;

import java.util.Map;

public interface CodeContract {

    interface Presenter extends BasePresenter {

        void validate(Map<ValidateType, String> needValidate);

    }

    interface View extends BaseView<Presenter> {

        void emailValidateError();
        void emailServerInvalidError();
        void passwordError();
        void confirmPasswordError();
        void codeError();
        void startLoad();
        void stopLoad();
        void showNetworkError();
    }
}
