package com.ltst.schoolapp.teacher.ui.checks.select.child.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivity;
import com.ltst.schoolapp.teacher.ui.checks.select.child.ChecksSelectChildActivityScope.ChecksSelectChildActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class ChecksSelectChildFragment extends CoreEnterFragment implements ChecksSelectChildContract.View {

    @Inject ChecksSelectChildPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.checks_select_child_recycler) RecyclerView recyclerView;
    @BindView(R.id.checks_select_child_next) Button next;
    @BindView(R.id.checks_select_child_progress) ViewGroup progressLayout;

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
        return R.layout.fragment_checks_select_child;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        ChecksSelectChildActivityComponent component = (ChecksSelectChildActivityComponent) root.getComponent();
        component.checksSelectChildComponent(
                new ChecksSelectChildScope.ChecksSelectChildModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.check_the_code_title);
    }

    @Override
    public void initToolbar(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

//    @OnClick(R.id.add_child_group_container)
//    void onChooseGroupClick() {
//        presenter.chooseGroup();
//    }

    @Override
    public void bindListeners(View.OnClickListener onNextClick) {
        next.setOnClickListener(onNextClick);
    }


    @Override
    public void bindAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setNextButtonEnabled(boolean isEnabled) {
        next.setEnabled(isEnabled);
        next.setClickable(isEnabled);
    }

    @Override public void oneGroupMode() {
//        groupsArrow.setVisibility(View.GONE);
//        selectGroupHeader.setText(getText(R.string.checks_select_group_one_group));
//        selectGroupHeader.setClickable(false);

    }

    @Override public void startLoad() {
        progressLayout.setVisibility(View.VISIBLE);
    }

    @Override public void stopLoad() {
        progressLayout.setVisibility(View.GONE);
    }

    @Override public void networkError() {
        dialogProvider.showNetError(getContext());
    }

    public static class Screen extends FragmentScreen {

        private final boolean isCheckIn;

        public Screen(boolean isCheckIn) {
            this.isCheckIn = isCheckIn;
        }

        @Override protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
            arguments.putBoolean(ChecksSelectChildActivity.Screen.KEY_IS_CHECK_IN, isCheckIn);
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
