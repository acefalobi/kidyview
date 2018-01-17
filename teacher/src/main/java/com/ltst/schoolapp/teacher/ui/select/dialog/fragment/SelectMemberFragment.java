package com.ltst.schoolapp.teacher.ui.select.dialog.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danil.recyclerbindableadapter.library.FilterBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.adapter.dialog.DialogItem;
import com.ltst.core.ui.holder.DialogMemberViewHolder;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.select.dialog.SelectDialogMemberScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnTextChanged;

public class SelectMemberFragment extends CoreFragment implements SelectMemberContract.View {

    @Inject SelectMemberPresenter presenter;

    @BindView(R.id.select_member_recycler_view) RecyclerView memberRecyclerView;

    private Toolbar toolbar;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_select_member;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        SelectDialogMemberScope.SelectDialogMemberComponent component =
                (SelectDialogMemberScope.SelectDialogMemberComponent) rootComponent.getComponent();
        component.selectMemeberComponent(new SelectMemberScope.SelectMemberModule(this)).inject(this);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        memberRecyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @OnTextChanged(R.id.select_member_search_edit_text)
    public void onSearchTextChanged(CharSequence text, int start, int before, int count) {
        presenter.searchText(text.toString());
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.new_message_title));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @Override public void addDomeMenuItem() {
        toolbar.inflateMenu(R.menu.menu_done);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                presenter.done();
            }
            return false;
        });
    }


    @Override public void setAdapter(FilterBindableAdapter<DialogItem, DialogMemberViewHolder> memberAdapter) {
        RecyclerView.Adapter adapter = memberRecyclerView.getAdapter();
        if (adapter == null || adapter != memberAdapter) {
            memberRecyclerView.setAdapter(memberAdapter);

        }
    }


    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new SelectMemberFragment();
        }
    }
}
