package com.ltst.schoolapp.teacher.ui.checks.code.fragment;

import android.text.TextWatcher;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ChecksCodeContract {

    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener);

        void bindListeners(TextWatcher codeWatcher,
                           android.view.View.OnClickListener onDoneClick);

        void setDoneEnabled(boolean isEnabled);


        void showCodeError();
    }
}
