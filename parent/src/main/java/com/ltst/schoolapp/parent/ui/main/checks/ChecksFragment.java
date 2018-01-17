package com.ltst.schoolapp.parent.ui.main.checks;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.EndlessRecyclerScrollListener;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.dated.checks.DatedChecksScope;
import com.ltst.schoolapp.parent.ui.main.BottomScreen;
import com.ltst.schoolapp.parent.ui.main.MainActivity;
import com.ltst.schoolapp.parent.ui.main.MainScope;

import javax.inject.Inject;

import butterknife.BindView;
import rx.functions.Action1;

public class ChecksFragment extends CoreFragment implements ChecksContract.View {

    @Inject ChecksPresenter presenter;
    @Inject DialogProvider dialogProvider;
    @BindView(R.id.checks_recycler) RecyclerView recyclerView;
    @BindView(R.id.checks_proress_bar) ProgressBar progressBar;
    @BindView(R.id.checks_empty) ViewGroup emptyScreen;
    @BindView(R.id.checks_swipe_to_refresh) SwipeRefreshLayout swipeToRefresh;
    private LinearLayoutManager layoutManager;
    private Bundle layoutManagerState = new Bundle();
    private Toolbar toolbar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_fix_checks;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        if (rootComponent instanceof MainActivity) {
            MainScope.MainComponent component = ((MainScope.MainComponent) rootComponent.getComponent());
            component.fixComponent(new ChecksScope.FixModule(this)).inject(this);
        } else {
            DatedChecksScope.DatedChecksComponent component =
                    ((DatedChecksScope.DatedChecksComponent) rootComponent.getComponent());
            component.checksComponent(new ChecksScope.FixModule(this)).inject(this);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        swipeToRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.apple_green));
        return view;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
//        Spinner spinner = (Spinner) toolbar.findViewById(R.id.feed_toolbar_spinner);
//        if (spinner != null) {
//            spinner.setVisibility(View.GONE);
//        }
        toolbar.setTitle(R.string.checks_screen_title);
        toolbar.inflateMenu(R.menu.menu_parent_checks);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.feed_menu_calendar) {
                presenter.openDatedChecksScreen();
            } else if (item.getItemId() == R.id.feed_menu_generate_code) {
                presenter.openGenerateCode();
            }
            return false;
        });
    }

    @Override
    public void initDatedToolbar(String date) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
        toolbar.getMenu().findItem(R.id.feed_menu_calendar).setVisible(false);
        toolbar.getMenu().findItem(R.id.feed_menu_generate_code).setVisible(false);
        toolbar.setTitle(date);
    }

    @Override
    public void stopRefresh() {
        swipeToRefresh.setRefreshing(false);
    }

    @Override public void disableSwipeToRefresh() {
        swipeToRefresh.setEnabled(false);
    }

    private static final String BUNDLE_RECYCLER_LAYOUT = "Checks.recycler.layout";

    @Override
    public void onStop() {
        super.onStop();
        layoutManagerState.putParcelable(BUNDLE_RECYCLER_LAYOUT, layoutManager.onSaveInstanceState());

    }

    @Override
    public void onStart() {
        super.onStart();
        Parcelable savedRecyclerLayoutState = layoutManagerState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        if (savedRecyclerLayoutState != null) {
            layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void init(RecyclerBindableAdapter adapter,
                     Action1<Integer> action,
                     SwipeRefreshLayout.OnRefreshListener refreshListener) {
        recyclerView.setAdapter(adapter);
        EndlessRecyclerScrollListener scrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            protected void onLoadMore(int page, int totalItemsCount) {
                action.call(totalItemsCount);
            }

            @Override
            protected View getProgressView() {
                return null;
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        swipeToRefresh.setOnRefreshListener(refreshListener);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyScreen() {
        emptyScreen.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmptyScreen() {
        emptyScreen.setVisibility(View.GONE);
    }

    @Override
    public void showNetError() {
        dialogProvider.showNetError(getContext());

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
