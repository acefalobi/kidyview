package com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Profile;
import com.ltst.core.util.validator.ValidateType;

import java.util.Map;

public interface ChangePasswordContract {

    interface Presenter extends BasePresenter {

        void validateAndUpdate(Map<ValidateType, String> needValidate);

        void openForgotPassword();
    }

    interface View extends BaseView<Presenter> {

        void bindData(Profile profile);

        void setToolbarNavigationIcon(int icon, android.view.View.OnClickListener onClickListener);

        void errorConfirmPassword();

        void errorOldPassword();

        void errorPassword();
    }
}
