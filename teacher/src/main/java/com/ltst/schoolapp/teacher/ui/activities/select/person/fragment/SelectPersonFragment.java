package com.ltst.schoolapp.teacher.ui.activities.select.person.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.activities.select.person.SelectPersonActivityScope.SelectPersonActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class SelectPersonFragment extends CoreEnterFragment implements SelectPersonContract.View {

    @Inject
    SelectPersonPresenter presenter;

    @BindView(R.id.select_person_recycler)
    RecyclerView recyclerView;

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
        return R.layout.fragment_select_person;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        SelectPersonActivityComponent component = (SelectPersonActivityComponent) root.getComponent();
        component.selectPersonComponent(new SelectPersonScope.SelectPersonModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.select_person_title);
        this.toolbar.inflateMenu(R.menu.menu_done);
    }

    @Override
    public void initToolbar(int icon, View.OnClickListener onClickListener,
                            Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    @Override
    public void bindAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new SelectPersonFragment();
        }
    }
}
