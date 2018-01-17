package com.ltst.schoolapp.parent.ui.checkout.select.school.fragment;


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

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.checkout.select.school.SelectSchoolScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectChildInSchoolFragment extends CoreFragment implements SelectChildInSchoolContract.View {

    @Inject SelectChildInSchoolPresenter presenter;
    @BindView(R.id.select_school_recycler) RecyclerView recyclerView;
    @BindView(R.id.select_school_next) Button nextButton;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_select_school;
    }

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        return view;

    }

    @OnClick(R.id.select_school_next)
    void onNextClick(){
        presenter.setResultAndClose();
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        SelectSchoolScope.SelectSchoolComponent component =
                ((SelectSchoolScope.SelectSchoolComponent) rootComponent.getComponent());
        component.sekectSchoolFragmentComponent(
                new SelectSchoolFragmentScope.SelectSchoolFragmentModule(this)).inject(this);
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.info_checkout_title));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @Override public void setNextButtonEnabled(boolean enable) {
        nextButton.setEnabled(enable);
    }

    @Override public void setAdapter(RecyclerBindableAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }


    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new SelectChildInSchoolFragment();
        }
    }
}
