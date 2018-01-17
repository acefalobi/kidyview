package com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment;

import android.support.v7.widget.RecyclerView;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ChecksSelectMemberContract {

    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener);

        void bindAdapter(RecyclerView.Adapter adapter);

        void setNextEnabled(boolean isEnabled);

        void bindListeners(android.view.View.OnClickListener onNextClick);

        void setHeaderText(boolean isCheckIn, String childrenNames, int size);
    }
}
