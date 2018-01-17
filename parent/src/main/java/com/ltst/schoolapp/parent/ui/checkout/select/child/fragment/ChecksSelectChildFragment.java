package com.ltst.schoolapp.parent.ui.checkout.select.child.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.checkout.select.child.ChecksSelectChildActivity;
import com.ltst.schoolapp.parent.ui.checkout.select.child.ChecksSelectChildActivityScope;

import javax.inject.Inject;

import butterknife.BindView;

public class ChecksSelectChildFragment extends CoreEnterFragment implements ChecksSelectChildContract.View {

    @Inject
    ChecksSelectChildPresenter presenter;

    @BindView(R.id.checks_select_child_recycler)
    RecyclerView recyclerView;
//    @BindView(R.id.checks_select_child_next)
//    Button next;

    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getBackgroundColorId() {
        return android.R.color.white;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_select_child;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        ChecksSelectChildActivityScope.ChecksSelectChildActivityComponent component = (ChecksSelectChildActivityScope.ChecksSelectChildActivityComponent) root.getComponent();
        component.checksSelectChildComponent(
                new ChecksSelectChildScope.ChecksSelectChildModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.info_checkout_title);
        this.toolbar.inflateMenu(R.menu.menu_done);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()== R.id.action_done) {
                    presenter.closeForResult();
                }
                return false;
            }
        });
    }

    @Override
    public void initToolbar(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    @Override
    public void bindListeners(View.OnClickListener onNextClick) {
//        next.setOnClickListener(onNextClick);
    }


    @Override
    public void bindAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setNextButtonEnabled(boolean isEnabled) {
        this.toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
    }

    public static class Screen extends FragmentScreen {

        private final boolean isCheckIn;

        public Screen(boolean isCheckIn) {
            this.isCheckIn = isCheckIn;
        }

        @Override protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
            arguments.putBoolean(ChecksSelectChildActivity.Screen.KEY_SELECTED_IDS, isCheckIn);
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ChecksSelectChildFragment();
        }
    }
}
