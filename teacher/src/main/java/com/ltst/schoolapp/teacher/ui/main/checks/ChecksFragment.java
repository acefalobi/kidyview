package com.ltst.schoolapp.teacher.ui.main.checks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.livetyping.library.CannyViewAnimator;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.adapter.ChecksAdapter;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.dated.DatedChecksScope;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;
import com.ltst.schoolapp.teacher.ui.main.MainScope;

import javax.inject.Inject;

import butterknife.BindView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;

public class ChecksFragment extends CoreFragment implements ChecksContract.View {

    @BindView(R.id.checks_animator)
    CannyViewAnimator animator;
    @BindView(R.id.checks_content)
    RealmRecyclerView realmRecyclerView;
    @BindView(R.id.checks_swipe_to_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.in_children_count)
    TextView inChildrenCount;
    @BindView(R.id.checks_fab)
    FloatingActionButton fab;
    @Inject
    ChecksPresenter presenter;
    private Toolbar toolbar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_checks;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        if (rootComponent instanceof MainActivity) {
            MainScope.MainComponent component = ((MainScope.MainComponent) rootComponent.getComponent());
            component.checksComponent(new ChecksScope.ChecksModule(this)).inject(this);
        } else {
            DatedChecksScope.DatedChecksComponent component =
                    (DatedChecksScope.DatedChecksComponent) rootComponent.getComponent();
            component.checksComponent(new ChecksScope.ChecksModule(this)).inject(this);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.apple_green));
        return view;
    }


    @Override
    protected int getBackgroundColorId() {
        return R.color.check_in_bg_blue;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);

        toolbar.inflateMenu(R.menu.menu_checks);
        toolbar.setOnMenuItemClickListener(presenter);

//        Spinner spinner = (Spinner) toolbar.findViewById(R.id.feed_toolbar_spinner);
//        if (spinner != null) {
//            spinner.setVisibility(View.VISIBLE);
//        }
        ImageView groupIcon = ((ImageView) toolbar.findViewById(R.id.main_toolbar_icon));
        if (groupIcon != null) {
            groupIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showContent() {
        animator.setDisplayedChildId(R.id.checks_content);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void showEmpty(boolean fromDateChecks) {
        swipeRefresh.setRefreshing(false);
        if (fromDateChecks) {
            animator.setDisplayedChildId(R.id.checks_dated_empty);
        } else {
            animator.setDisplayedChildId(R.id.checks_empty);
        }

    }

    @Override
    public void bindListeners(View.OnClickListener onFabClick,
                              Toolbar.OnMenuItemClickListener onMenuItemClick,
                              RealmRecyclerView.OnLoadMoreListener onLoadMore,
                              SwipeRefreshLayout.OnRefreshListener refreshListener) {
        fab.setOnClickListener(onFabClick);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        realmRecyclerView.enableShowLoadMore();
        realmRecyclerView.setOnLoadMoreListener(onLoadMore);
        swipeRefresh.setOnRefreshListener(refreshListener);
    }

    @Override
    public void initDatedChecksToolbar(String title, boolean datedChecks) {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
        getView().findViewById(R.id.checks_fab).setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.feed_menu_calendar).setVisible(false);
//        if (datedChecks) {
//        Spinner spinner = (Spinner) toolbar.findViewById(R.id.feed_toolbar_spinner);
//        spinner.setVisibility(View.GONE);
//        ImageView groupIcon = ((ImageView) toolbar.findViewById(R.id.main_toolbar_icon));
//        groupIcon.setVisibility(View.GONE);
//        }

    }

    @Override
    public void stopRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    public void enableLoadMore(boolean isEnabled) {
        if (isEnabled) {
            realmRecyclerView.enableShowLoadMore();
        } else {
            realmRecyclerView.disableShowLoadMore();
        }
    }

    @Override
    public void bindAdapter(ChecksAdapter checksAdapter, int checkedIn) {
        realmRecyclerView.setAdapter(checksAdapter);
        inChildrenCount.setText(" " + String.valueOf(checkedIn));
    }


    public static class Screen extends BottomNavigationFragmentScreen {

        @Override
        public int unselectedIconId() {
            return R.drawable.ic_check_unselected;
        }

        @Override
        public int selectedIconId() {
            return R.drawable.ic_check_selected;
        }

        @Override
        public String getName() {
            return BottomScreen.CHECKS.toString();
        }

        @Override
        protected Fragment createFragment() {
            return new ChecksFragment();
        }
    }
}
