package com.ltst.schoolapp.parent.ui.school.info.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.school.info.SchoolScope;

import javax.inject.Inject;

import butterknife.BindView;

public class SchoolInfoFragment extends CoreFragment implements SchoolInfoContract.View {

    @Inject SchoolInfoPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.school_info_progress_bar) ProgressBar progressBar;
    @BindView(R.id.school_info_recycler_view) RecyclerView recyclerView;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_school_info;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        SchoolScope.SchoolComponent component = (SchoolScope.SchoolComponent) rootComponent.getComponent();
        component.infoComponent(new SchoolInfoScope.InfoModule(this)).inject(this);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);
        return view;

    }

    @Override protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.school_info_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @Override public void setAdapter(RecyclerBindableAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }


    @Override public void netError() {
        dialogProvider.showNetError(getContext());
    }

    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new SchoolInfoFragment();
        }
    }
}
