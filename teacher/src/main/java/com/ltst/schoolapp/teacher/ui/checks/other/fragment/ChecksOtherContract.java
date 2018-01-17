package com.ltst.schoolapp.teacher.ui.checks.other.fragment;

import android.text.TextWatcher;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ChecksOtherContract {

    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener);

        void bindListeners(TextWatcher firstNameWatcher,
                           TextWatcher lastNameWatcher,
                           android.view.View.OnClickListener onNextClick);

        void setNextEnabled(boolean isEnabled);
    }
}
