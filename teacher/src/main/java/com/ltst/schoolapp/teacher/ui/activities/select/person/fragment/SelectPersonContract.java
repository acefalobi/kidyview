package com.ltst.schoolapp.teacher.ui.activities.select.person.fragment;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface SelectPersonContract {

    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener, Toolbar.OnMenuItemClickListener onMenuItemClickListener);

        void bindAdapter(RecyclerView.Adapter adapter);
    }
}
