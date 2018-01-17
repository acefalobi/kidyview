package com.ltst.schoolapp.parent.ui.checkout.select.child.fragment;

import android.support.v7.widget.RecyclerView;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ChecksSelectChildContract {

    interface Presenter extends BasePresenter {

        void closeForResult();
    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener);

        void bindListeners(android.view.View.OnClickListener onNextClick);

        void bindAdapter(RecyclerView.Adapter adapter);

        void setNextButtonEnabled(boolean isDisabled);
    }
}
