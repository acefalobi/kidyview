package com.ltst.schoolapp.teacher.ui.checks.single.check.fragment;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface SingleCheckContract {

    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener);

        void bindAdapter(RecyclerBindableAdapter adapter);

        void addHeader(RecyclerBindableAdapter adapter);

        void scrollToBottom();
    }
}
