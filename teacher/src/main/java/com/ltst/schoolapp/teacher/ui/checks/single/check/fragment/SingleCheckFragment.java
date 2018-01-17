package com.ltst.schoolapp.teacher.ui.checks.single.check.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.single.check.SingleCheckActivityScope.SingleCheckActivityComponent;
import com.ltst.schoolapp.teacher.ui.checks.single.check.fragment.SingleCheckScope.SingleCheckModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class SingleCheckFragment extends CoreEnterFragment implements SingleCheckContract.View {

    @Inject
    SingleCheckPresenter presenter;

    @BindView(R.id.single_check_recycler) RecyclerView recyclerView;

    private Toolbar toolbar;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }


    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_single_check;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        SingleCheckActivityComponent component = (SingleCheckActivityComponent) root.getComponent();
        component.singleCheckComponent(new SingleCheckModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.check_the_code_title);
    }

    @Override protected int getBackgroundColorId() {
        return R.color.pale_gray;
    }

    @Override
    public void initToolbar(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    @OnClick(R.id.single_check_done)
    void onDoneClick() {
        presenter.goBack();
    }

    @Override
    public void bindAdapter(RecyclerBindableAdapter adapter) {
        recyclerView.setAdapter(adapter);
        addOkFooter(adapter);


    }

    private void addOkFooter(RecyclerBindableAdapter adapter) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View footer = inflater.inflate(R.layout.layout_single_check_footer, recyclerView, false);
        adapter.addFooter(footer);
    }

    @Override
    public void addHeader(RecyclerBindableAdapter adapter) {
        if (adapter.getHeadersCount() == 0 && adapter.getRealItemCount() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            int headerId = R.layout.fragment_check_item_date;
            ViewGroup header = (ViewGroup) inflater.inflate(headerId, recyclerView, false);
            TextView headerText = (TextView) header.getChildAt(0);
            ChildCheck check = (ChildCheck) adapter.getItem(0);
            headerText.setText(DateUtils.getDayOfTextMonthString(check.getDatetime(), getContext()));
            adapter.addHeader(header);
        }
    }

    @Override public void scrollToBottom() {
        layoutManager.scrollToPosition(recyclerView.getAdapter().getItemCount());
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new SingleCheckFragment();
        }
    }
}
