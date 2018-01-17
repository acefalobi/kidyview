package com.ltst.schoolapp.teacher.ui.main.children;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.danil.recyclerbindableadapter.library.FilterBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainScope;

import javax.inject.Inject;

import butterknife.BindView;

public class ChildrenFragment extends CoreFragment implements ChildrenContract.View {


    @Inject
    ChildrenPresenter presenter;

    @Inject
    DialogProvider dialogProvider;

    @BindView(R.id.children_load_container)
    ViewGroup loadContainer;
    @BindView(R.id.children_list)
    RecyclerView childrenList;
    @BindView(R.id.children_empty_container)
    ViewGroup emptyContainer;


    private Toolbar toolbar;
    private SearchView searchView;
    private LinearLayoutManager layoutManager;


    @Override
    protected int getBackgroundColorId() {
        return R.color.check_in_bg_blue;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_children;
    }

    @Override
    protected void onCreateComponent(HasSubComponents hasSubComponents) {
        MainScope.MainComponent mainComponent = (MainScope.MainComponent) hasSubComponents.getComponent();
        mainComponent.childrenComponent(new ChildrenScope.ChildrenModule(this))
                .inject(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        layoutManager = new LinearLayoutManager(getContext());
        childrenList.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
        toolbar.inflateMenu(R.menu.menu_children);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_plus) {
                presenter.goToAddChildScreen();
            }
            return false;
        });
//        Spinner spinner = (Spinner) toolbar.findViewById(R.id.feed_toolbar_spinner);
//        spinner.setVisibility(View.VISIBLE);
        ImageView groupIcon = ((ImageView) toolbar.findViewById(R.id.main_toolbar_icon));
        groupIcon.setVisibility(View.VISIBLE);
        MenuItem search = toolbar.getMenu().findItem(R.id.action_search);
        searchView = (android.support.v7.widget.SearchView)
                search.getActionView();
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                search.collapseActionView();
                searchView.setQuery("", false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.setSearch(newText);
                return false;
            }
        });

    }


    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showLoad() {
        toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
        loadContainer.setVisibility(View.VISIBLE);
        emptyContainer.setVisibility(View.GONE);
    }

    @Override
    public void showEmpty() {
        MenuItem item = toolbar.getMenu().findItem(R.id.action_search);
        if (item != null) {
            item.setVisible(false);
        }
        loadContainer.setVisibility(View.GONE);
        childrenList.setVisibility(View.GONE);
        emptyContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showList(Integer scrollState) {
        MenuItem item = toolbar.getMenu().findItem(R.id.action_search);
        if (item != null) {
            item.setVisible(true);
        }
        childrenList.setVisibility(View.VISIBLE);
        loadContainer.setVisibility(View.GONE);
        if (scrollState != null) {
            layoutManager.scrollToPosition(scrollState);
        }
    }

    @Override
    public void setAdapter(FilterBindableAdapter adapter) {
        childrenList.setAdapter(adapter);
    }

    @Override
    public void deleteChildWarning(String childName) {
        String messageFormat = getString(R.string.children_delete_format);
        dialogProvider.emptyGroupsWarning(String.format(messageFormat, childName), true,
                (dialog, which) -> {
                    presenter.deleteSelectedChild();

                });
    }

    @Override
    public void networkError() {
        dialogProvider.showNetError(getContext());
    }


    public static final class Screen extends BottomNavigationFragmentScreen {

        @Override
        public int unselectedIconId() {
            return R.drawable.ic_children_unselected;
        }

        @Override
        public int selectedIconId() {
            return R.drawable.ic_children_selected;
        }

        @Override
        public String getName() {
            return BottomScreen.CHILDREN.toString();
        }

        @Override
        protected Fragment createFragment() {
            return new ChildrenFragment();
        }
    }
}
