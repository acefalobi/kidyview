package com.ltst.schoolapp.teacher.ui.checks.code.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.code.ChecksCodeActivityScope.ChecksCodeActivityComponent;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;

public class ChecksCodeFragment extends CoreEnterFragment implements ChecksCodeContract.View {

    @Inject
    ChecksCodePresenter presenter;

    @BindView(R.id.checks_code_enter)
    MaterialEditText code;
    @BindView(R.id.checks_code_done)
    Button done;
    private Toolbar toolbar;

    @BindString(R.string.code_code_error)
    String codeError;

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
        return android.R.color.white;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_checks_code;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        ChecksCodeActivityComponent component = (ChecksCodeActivityComponent) root.getComponent();
        component.checksCodeComponent(new ChecksCodeScope.ChecksCodeModule(this)).inject(this);
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
    public void bindListeners(TextWatcher codeWatcher,
                              android.view.View.OnClickListener onDoneClick) {
        code.addTextChangedListener(codeWatcher);
        done.setOnClickListener(onDoneClick);
    }

    @Override
    public void setDoneEnabled(boolean isEnabled) {
        if (done.isEnabled() == isEnabled) return;
        done.setEnabled(isEnabled);
        done.setClickable(isEnabled);
    }

    @Override
    public void showCodeError() {
        code.setError(codeError);
    }


    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ChecksCodeFragment();
        }
    }
}
