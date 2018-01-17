package com.ltst.schoolapp.parent.ui.main.checks;

import android.support.v4.widget.SwipeRefreshLayout;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

import rx.functions.Action1;

public interface ChecksContract {

    interface Presenter extends BasePresenter {

        void openDatedChecksScreen();

        void goBack();

        void openGenerateCode();
    }

    interface View extends BaseView<Presenter> {

        void init(RecyclerBindableAdapter adapter,
                  Action1<Integer> integerAction1,
                  SwipeRefreshLayout.OnRefreshListener refreshListener);

        void startLoad();

        void stopLoad();

        void showEmptyScreen();

        void hideEmptyScreen();

        void showNetError();

        void initDatedToolbar(String date);

        void stopRefresh();

        void disableSwipeToRefresh();

    }

}
