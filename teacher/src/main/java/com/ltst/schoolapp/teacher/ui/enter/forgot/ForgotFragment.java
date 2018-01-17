package com.ltst.schoolapp.teacher.ui.enter.forgot;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.enter.EnterScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class ForgotFragment extends CoreEnterFragment implements ForgotContract.View {

    @Inject ForgotPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.forgot_email_field) MaterialEditText emailField;
    @BindView(R.id.forgot_progress_bar) ProgressBar progressBar;
    @BindView(R.id.forgot_button) Button resetButton;


    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }


    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_forgot;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EnterScope.EnterComponent component = (EnterScope.EnterComponent) rootComponent.getComponent();
        component.forgotComponent(new ForgotScope.ForgotModule(this)).inject(this);
    }

    @OnClick(R.id.forgot_button)
    void resetButton() {
        presenter.done();
    }

    @OnTextChanged(R.id.forgot_email_field)
    void onNameEmail(CharSequence text, int start, int before, int count) {
        presenter.setEmail(text.toString());
    }

    @OnFocusChange(R.id.forgot_email_field)
    void onEmailFocusChanged(MaterialEditText emailField, boolean focus) {
        if (focus) {
            emailField.setError(null);
        }
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void emailRegexError() {
        emailField.setError(getString(R.string.enter_email_error));
    }

    @Override
    public void emailNotExist() {
        emailField.setError(getString(R.string.forgot_email_not_exist));
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
        resetButton.setEnabled(true);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        resetButton.setEnabled(false);
    }

    @Override
    public void showNetworkError() {
        dialogProvider.showNetError(getContext());
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ForgotFragment();
        }
    }
}
