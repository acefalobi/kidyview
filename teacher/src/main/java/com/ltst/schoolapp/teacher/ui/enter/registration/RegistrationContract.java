package com.ltst.schoolapp.teacher.ui.enter.registration;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.util.validator.ValidateType;

import java.util.Map;

public interface RegistrationContract {

    interface Presenter extends BasePresenter {

        void validate(Map<ValidateType, String> needValidate);

    }

    interface View extends BaseView<Presenter> {

        void networkError();

        void errorRegexEmail();

        void errorExistEmail();

        void errorPassword();

        void errorConfirmPassword();

        void showLoad();

        void showContent();

    }
}
