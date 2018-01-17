package com.ltst.schoolapp.parent.ui.enter.login;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.enter.EnterScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class LoginFragment extends CoreFragment implements LoginContract.View {

    @Inject LoginPresenter presenter;

    @Inject DialogProvider dialogProvider;

    @BindView(R.id.login_email_field) MaterialEditText emailField;
    @BindView(R.id.login_password_field) MaterialEditText passwordField;
    @BindView(R.id.edit_profile_progress_bar) ProgressBar progressBar;
    @BindView(R.id.login_button) Button loginButton;
    @BindView(R.id.login_forgot_password) TextView forgotPassText;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EnterScope.EnterComponent component = (EnterScope.EnterComponent) rootComponent.getComponent();
        component.loginComponent(new LoginScope.LoginModule(this)).inject(this);
    }

    @Override
    protected int getBackgroundColorId() {
        return R.color.enter_background;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {

    }

    @OnClick(R.id.login_button)
    void onLoginClick() {
        presenter.done();
    }

    @OnClick(R.id.login_forgot_password)
    void onForgotClick(){
        presenter.forgotPassword();
    }

    @OnFocusChange({R.id.login_email_field, R.id.login_password_field})
    void onFieldFocusChanged(MaterialEditText field, boolean focus) {
        if (focus) {
            field.setError(null);
        }
    }

    @OnTextChanged(R.id.login_email_field)
    void OnEmailTextChanged(CharSequence text, int start, int before, int count) {
        presenter.setEmail(text.toString());
    }

    @OnTextChanged(R.id.login_password_field)
    void onPasswordTextChanged(CharSequence text, int start, int before, int count) {
        presenter.setPassword(text.toString());
    }

    @Override
    public void emailValidationError() {
        emailField.setError(getString(R.string.enter_email_error));
    }

    @Override
    public void emptyPassword() {
        passwordField.setError(getString(R.string.empty_field_error));
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        forgotPassText.setEnabled(false);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        forgotPassText.setEnabled(true);
    }

    @Override
    public void netError() {
        dialogProvider.showNetError(getContext());
    }

    @Override
    public void serverLoginError() {
        dialogProvider.showLoginError(getContext());
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new LoginFragment();
        }
    }
}
