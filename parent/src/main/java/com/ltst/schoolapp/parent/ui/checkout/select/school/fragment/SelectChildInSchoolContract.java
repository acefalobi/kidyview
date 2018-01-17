package com.ltst.schoolapp.parent.ui.checkout.select.school.fragment;


import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface SelectChildInSchoolContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void setResultAndClose();
    }

    interface View extends BaseView<Presenter> {

        void setNextButtonEnabled(boolean enable);

        void setAdapter(RecyclerBindableAdapter adapter);

    }
}
