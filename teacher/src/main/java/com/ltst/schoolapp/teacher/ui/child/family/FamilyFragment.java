package com.ltst.schoolapp.teacher.ui.child.family;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.child.ChildScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyFragment extends CoreFragment implements FamilyContract.View {

    @Inject FamilyPresenter presenter;

    @BindView(R.id.family_root_view) ViewGroup rootView;
    @BindView(R.id.fmaily_progress_bar) ProgressBar progressBar;
    @BindView(R.id.family_list) RecyclerView recyclerView;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_family;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        ChildScope.ChildComponent component = (ChildScope.ChildComponent) rootComponent.getComponent();
        component.familyComponent(new FamilyScope.FamilyModule(this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
        toolbar.setTitle(getString(R.string.family_title));
    }

    @Override
    public void setAdapter(RecyclerBindableAdapter adapter) {
        recyclerView.setAdapter(adapter);
        if (adapter.getFootersCount() < 1) {
            LinearLayout familyFooter = ((LinearLayout) LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_family_footer, recyclerView, false));
            adapter.addFooter(familyFooter);
            familyFooter.setOnClickListener(v -> {
                presenter.openAddMember();
            });

        }
    }

    @Override
    protected int getBackgroundColorId() {
        return R.color.under_card_background;
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void showChangeStatusPopup(Member member, RecyclerBindableAdapter adapter) {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_change_status_popup, rootView, false);
        RecyclerView recyclerView = ButterKnife.findById(view, R.id.change_status_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            presenter.changeStatusForMember();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            presenter.dismissChangeStatus();
        });
        String titleFormat = getString(R.string.family_member_change_status_title);

        builder.setTitle(String.format(titleFormat, member.getFirstName(), member.getLastName()));
        builder.create().show();
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new FamilyFragment();
        }
    }
}
