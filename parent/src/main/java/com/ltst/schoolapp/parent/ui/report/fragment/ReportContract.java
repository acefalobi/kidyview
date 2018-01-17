package com.ltst.schoolapp.parent.ui.report.fragment;


import android.support.annotation.DrawableRes;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.ChildState;

public interface ReportContract {

    interface Presenter extends BasePresenter {

        void goBack();
    }

    interface View extends BaseView<Presenter> {
        void bindData(RecyclerBindableAdapter adapter, @DrawableRes int headerIconResId, String childName);

        void startLoad();

        void stopLoad();

        void bindCheckOut(ChildState checkOut);

        void bindCheckIn(ChildState checkIn);

    }
}
