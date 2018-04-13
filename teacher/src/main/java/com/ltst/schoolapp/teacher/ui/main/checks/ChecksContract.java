package com.ltst.schoolapp.teacher.ui.main.checks;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.ui.adapter.ChecksAdapter;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;

public interface ChecksContract {

    interface Presenter extends BasePresenter {

        void openSortChecksCalendar();

        void goBack();
    }

    interface View extends BaseView<Presenter> {

        void showContent();

        void showLoading();

        void showEmpty(boolean fromDateChecks);

        void bindListeners(android.view.View.OnClickListener onFabClick,
                           Toolbar.OnMenuItemClickListener onMenuItemClick,
                           RealmRecyclerView.OnLoadMoreListener onLoadMore,
                           SwipeRefreshLayout.OnRefreshListener refreshListener);

        void initDatedChecksToolbar(String title, boolean fromDatedChecks);

        void stopRefresh();

        void enableLoadMore(boolean isEnabled);

        void bindAdapter(ChecksAdapter checksAdapter, int checkedIn);
    }
}
