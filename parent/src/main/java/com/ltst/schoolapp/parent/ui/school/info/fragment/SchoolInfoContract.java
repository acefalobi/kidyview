package com.ltst.schoolapp.parent.ui.school.info.fragment;


import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface SchoolInfoContract {

    interface Presenter extends BasePresenter {

        void goBack();
    }

    interface View extends BaseView<Presenter> {

        void setAdapter(RecyclerBindableAdapter adapter);

        void startLoad();

        void stopLoad();

        void netError();
    }
}
