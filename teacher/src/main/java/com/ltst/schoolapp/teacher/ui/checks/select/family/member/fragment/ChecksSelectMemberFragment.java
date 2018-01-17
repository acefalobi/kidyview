package com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment;

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
import android.widget.TextView;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.ChecksSelectMemberActivityScope.ChecksSelectMemberActivityComponent;
import com.ltst.schoolapp.teacher.ui.checks.select.family.member.fragment.ChecksSelectMemberScope.ChecksSelectMemberModule;

import javax.inject.Inject;

import butterknife.BindView;

public class ChecksSelectMemberFragment extends CoreEnterFragment implements ChecksSelectMemberContract.View {

    @Inject
    ChecksSelectMemberPresenter presenter;

    @BindView(R.id.checks_select_member_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.checks_select_member_next)
    Button next;
    @BindView(R.id.checks_select_member_header) TextView checkTypeHeader;

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
        return R.layout.fragment_checks_select_member;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        ChecksSelectMemberActivityComponent component = (ChecksSelectMemberActivityComponent) root.getComponent();
        component.checksSelectMemberComponent(new ChecksSelectMemberModule(this)).inject(this);
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
    public void bindAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setNextEnabled(boolean isEnabled) {
        next.setEnabled(isEnabled);
        next.setClickable(isEnabled);
    }

    @Override
    public void bindListeners(View.OnClickListener onNextClick) {
        next.setOnClickListener(onNextClick);
    }

    @Override public void setHeaderText(boolean isCheckIn, String childrenNames, int parentSize) {
        String headerText;
        if (parentSize == 0) {
            headerText = getString(R.string.checks_select_member_empty_list_header);
        } else {
            String headerTextFormat = getString(isCheckIn
                    ? R.string.checks_in_select_member_text
                    : R.string.checks_out_select_member_text);
            headerText = String.format(headerTextFormat, childrenNames);
        }

        checkTypeHeader.setText(headerText);
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ChecksSelectMemberFragment();
        }
    }
}
