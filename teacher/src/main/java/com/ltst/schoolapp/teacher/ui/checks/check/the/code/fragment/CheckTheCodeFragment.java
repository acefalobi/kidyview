package com.ltst.schoolapp.teacher.ui.checks.check.the.code.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.check.the.code.CheckTheCodeActivityScope.CheckTheCodeActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class CheckTheCodeFragment extends CoreEnterFragment implements CheckTheCodeContract.View {

    @Inject
    CheckTheCodePresenter presenter;

    @BindView(R.id.check_the_code_check_in) View checkIn;
    @BindView(R.id.check_the_code_check_out) View checkOut;
    @BindView(R.id.check_the_code_group_title) TextView groupTitleFiled;

    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getBackgroundColorId() {
        return R.color.gray_background;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_check_the_code;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        CheckTheCodeActivityComponent component = (CheckTheCodeActivityComponent) root.getComponent();
        component.checkTheCodeComponent(new CheckTheCodeScope
                .CheckTheCodeModule(this)).inject(this);
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

    @Override
    public void bindListeners(View.OnClickListener onCheckInClick,
                              View.OnClickListener onCheckOutClick) {
        checkIn.setOnClickListener(onCheckInClick);
        checkOut.setOnClickListener(onCheckOutClick);
    }

    @Override public void bindGroupTitle(String selectedGroupTitle) {
        groupTitleFiled.setText(selectedGroupTitle);
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new CheckTheCodeFragment();
        }

        @Override protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
        }
    }
}
