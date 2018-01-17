package com.ltst.schoolapp.teacher.ui.checks.select.child.fragment;

import android.support.v7.widget.RecyclerView;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ChecksSelectChildContract {

    interface Presenter extends BasePresenter {

//        void chooseGroup();

//        void changeGroup();
    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener);

        void bindListeners(android.view.View.OnClickListener onNextClick);

        void bindAdapter(RecyclerView.Adapter adapter);

        void setNextButtonEnabled(boolean isDisabled);

//        void showChooseGroupDilaog(RecyclerBindableAdapter<SelectableGroup, BindableViewHolder> groupsAdapter);


//        void setSelectedGroup(String title);

        void oneGroupMode();

        void startLoad();

        void stopLoad();

        void networkError();
    }
}
