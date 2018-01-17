package com.ltst.schoolapp.teacher.ui.main.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.livetyping.library.CannyViewAnimator;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.EndlessRecyclerScrollListener;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.activities.dated.feed.DatedFeedScope.DatedFeedComponent;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;
import com.ltst.schoolapp.teacher.ui.main.MainScope.MainComponent;

import javax.inject.Inject;

import butterknife.BindView;
import rx.functions.Action1;

import static android.text.style.DynamicDrawableSpan.ALIGN_BASELINE;

public class FeedFragment extends CoreFragment implements FeedContract.View {

    @Inject
    FeedPresenter presenter;

    @BindView(R.id.feed_animator)
    CannyViewAnimator animator;

    @BindView(R.id.feed_empty_text)
    TextView emptyText;

    @BindView(R.id.feed_content)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feed_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.feed_fab)
    FloatingActionButton floatingActionButton;

    private Toolbar toolbar;
    private Spinner toolbarSpinner;
    private SearchView toolbarSearch;
    private EndlessRecyclerScrollListener scrollListener;
    private MenuItem searchMenuItem;
    private boolean isMain;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (presenter != null) {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        emptyText.setText(getEmptyText());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.apple_green));
        return root;
    }

    @Override protected int getBackgroundColorId() {
        return R.color.pale_gray;
    }

    private Spannable getEmptyText() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String textFirst = getString(R.string.feed_empty_text_1);
        String textSecond = getString(R.string.feed_empty_text_2);
        ImageSpan calendar = new ImageSpan(getContext(), R.drawable.ic_calendar, ALIGN_BASELINE);
        ImageSpan search = new ImageSpan(getContext(), R.drawable.ic_search_blue, ALIGN_BASELINE);
        builder.append(textFirst);
        builder.setSpan(search, builder.length() - 1, builder.length(), 0);
        builder.append(textSecond);
        builder.setSpan(calendar, builder.length() - 1, builder.length(), 0);
        return builder;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }


    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_feed;
    }

    @Override
    protected void onCreateComponent(HasSubComponents hasSubComponents) {
        if (hasSubComponents instanceof MainActivity) {
            isMain = true;
            MainComponent component = (MainComponent) hasSubComponents.getComponent();
            component.feedcomponent(new FeedScope.FeedModule(this, isMain)).inject(this);
        } else {
            isMain = false;
            DatedFeedComponent component = (DatedFeedComponent) hasSubComponents.getComponent();
            component.feedComponent(new FeedScope.FeedModule(this, isMain)).inject(this);
        }
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        if (!isMain) return;
        this.toolbar.inflateMenu(R.menu.menu_feed);
//        toolbarSpinner = (Spinner) toolbar.findViewById(R.id.feed_toolbar_spinner);
//        toolbarSpinner.setVisibility(View.VISIBLE);
        ImageView groupIcon = ((ImageView) toolbar.findViewById(R.id.main_toolbar_icon));
        groupIcon.setVisibility(View.VISIBLE);
        searchMenuItem = toolbar.getMenu().findItem(R.id.feed_menu_search);
        toolbarSearch = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
    }


    @Override
    public void initToolbar(int navigationIcon, View.OnClickListener onNavigationClick,
                            Toolbar.OnMenuItemClickListener listener, String title) {
        if (navigationIcon != 0)
            toolbar.setNavigationIcon(navigationIcon);
        toolbar.setNavigationOnClickListener(onNavigationClick);
        toolbar.setOnMenuItemClickListener(listener);
        if (title != null)
            toolbar.setTitle(title);
    }

    @Override
    public void bindData(RecyclerView.Adapter adapter,
                         SwipeRefreshLayout.OnRefreshListener onRefreshListener,
                         SearchView.OnQueryTextListener searchListener,
                         Action1<Void> onCollapseSearch,
                         View.OnClickListener onFabClickListener,
                         Action1<Integer> onLoadMore) {
        RecyclerView.Adapter existAdapter = recyclerView.getAdapter();
        if (existAdapter == null || existAdapter !=adapter) {
            recyclerView.setAdapter(adapter);
        }

        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        floatingActionButton.setOnClickListener(onFabClickListener);
        scrollListener = new EndlessRecyclerScrollListener(recyclerView.getLayoutManager()) {
            @Override
            protected void onLoadMore(int page, int totalItemsCount) {
                onLoadMore.call(totalItemsCount);
            }

            @Override
            protected View getProgressView() {
                return null;
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        if (!isMain) return;
//        initSpinner(spinnerAdapter, spinnerListener);
        toolbarSearch.setOnQueryTextListener(searchListener);
        toolbarSearch.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchMenuItem.collapseActionView();
                toolbarSearch.setQuery("", false);
                onCollapseSearch.call(null);
            }
        });

    }

    @Override
    public void showContent() {
        swipeRefreshLayout.setRefreshing(false);
        animator.setDisplayedChildId(R.id.feed_content);
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void showEmpty() {
        swipeRefreshLayout.setRefreshing(false);
        animator.setDisplayedChildId(R.id.feed_empty);
    }

    @Override
    public void setSpinnerPosition(int position) {
        toolbarSpinner.setSelection(position);
    }

    @Override
    public void setPaginationIsEnd(boolean isEnd) {
        if (isEnd) {
            scrollListener.setIsEnd();
        } else {
            scrollListener.clear();
        }
    }

    @Override
    public void expandSearch(boolean isExpand) {
        if (searchMenuItem == null) return;
        if (isExpand) {
            searchMenuItem.expandActionView();
        } else {
            searchMenuItem.collapseActionView();
        }
    }

    public static final class Screen extends BottomNavigationFragmentScreen {

        @Override
        public String getName() {
            return BottomScreen.FEED.toString();
        }

        @Override
        protected Fragment createFragment() {
            return new FeedFragment();
        }

        @Override
        public int unselectedIconId() {
            return R.drawable.ic_feed_unselected;
        }

        @Override
        public int selectedIconId() {
            return R.drawable.ic_feed_selected;
        }

    }
}
