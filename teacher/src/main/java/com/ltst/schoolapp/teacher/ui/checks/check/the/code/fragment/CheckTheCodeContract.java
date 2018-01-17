package com.ltst.schoolapp.teacher.ui.checks.check.the.code.fragment;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface CheckTheCodeContract {

    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener);

        void bindListeners(android.view.View.OnClickListener onCheckInClick,
                           android.view.View.OnClickListener onCheckOutClick);

        void bindGroupTitle(String selectedGroupTitle);
    }
}
