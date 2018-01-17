package com.ltst.schoolapp.parent.ui.enter.newpass;

import android.os.Bundle;
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
import com.ltst.schoolapp.parent.ui.enter.EnterScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class NewPassFragment extends CoreEnterFragment implements NewPassContract.View {

    @Inject NewPassPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.new_pass_email) MaterialEditText emailField;
    @BindView(R.id.new_pass_code) MaterialEditText codeField;
    @BindView(R.id.new_pass_password) MaterialEditText passwordField;
    @BindView(R.id.new_pass_progress_bar) ProgressBar progressBar;
    @BindView(R.id.new_pass_button) Button recoveryButton;

    private Bundle screenArguments;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_newpass;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        screenArguments = getArguments();
        EnterScope.EnterComponent component = (EnterScope.EnterComponent) rootComponent.getComponent();
        component.newPassComponent(new NewPassScope.NewPassModule(this, screenArguments)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
//        toolbar.setVisibility(View.GONE);
    }

    @OnTextChanged(R.id.new_pass_code)
    void onCodeChanged(CharSequence text, int start, int before, int count) {
        presenter.setCode(text.toString());
    }

    @OnTextChanged(R.id.new_pass_password)
    void onPasswordChanged(CharSequence text, int start, int before, int count) {
        presenter.setPassword(text.toString());
    }

//    @OnClick(R.id.new_pass_back_arrow)
//    void onBackPressed() {
//        presenter.goBack();
//    }

    @OnClick(R.id.new_pass_button)
    void onRecoveryClick() {
        presenter.checkData();
    }

    @Override
    public void validatePasswordError() {
        passwordField.setError(getString(R.string.enter_password_error));
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        recoveryButton.setEnabled(false);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
        recoveryButton.setEnabled(true);
    }

    @Override
    public void showNetworkError() {
        dialogProvider.showNetError(getContext());
    }

    @Override
    public void setEmail(String email) {
        emailField.setText(email);
        emailField.setFocusable(false);
    }

    @Override
    public void noEmailError() {
        emailField.setError(getString(R.string.new_pass_email_not_exist));
    }

    @Override
    public void wrongNumberError() {
        codeField.setError(getString(R.string.new_pass_wrong_code_field));
    }

    public static class Screen extends FragmentScreen {

        public static final String RESTORE_PASS_EMAIL = "NewPassFragment.Email";

        private final String email;

        public Screen(String email) {
            this.email = email;
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
            arguments.putString(RESTORE_PASS_EMAIL, email);
        }

        @Override
        protected Fragment createFragment() {
            return new NewPassFragment();
        }
    }
}
