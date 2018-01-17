package com.ltst.schoolapp.parent.ui.enter.registration;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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

public class RegistrationFragment extends CoreFragment implements RegistrationContract.View {

    @Inject DialogProvider dialogProvider;

    @Inject RegistrationPresenter presenter;

    @BindView(R.id.code_email_field) MaterialEditText emailField;
    @BindView(R.id.code_password_field) MaterialEditText passwordField;
    @BindView(R.id.code_confirm_password_field) MaterialEditText passConfirmField;
    @BindView(R.id.code_code_field) MaterialEditText codeField;
    @BindView(R.id.registration_progress_bar) ProgressBar progressBar;

    @BindView(R.id.code_generate_code) Button sendAgainButton;
    @BindView(R.id.code_login) Button loginButton;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_registration_new;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EnterScope.EnterComponent component = (EnterScope.EnterComponent) rootComponent.getComponent();
        component.registrationComponent(new RegistrationScope.Module(this)).inject(this);
    }

    @OnFocusChange({R.id.code_email_field, R.id.code_password_field,
                           R.id.code_confirm_password_field, R.id.code_code_field})
    void onFieldFocusChanged(MaterialEditText editText, boolean isFocus) {
        if (isFocus) {
            editText.setError(null);
        }
    }

    @OnClick(R.id.code_login)
    void onLoginClick() {
        presenter.login();
    }

    @OnClick(R.id.code_generate_code)
    void onSendCodeAgainClick() {
        presenter.sendCodeAgain();
    }

    @OnTextChanged(R.id.code_email_field)
    void onTextInFieldChanged(CharSequence text, int start, int before, int count) {
        presenter.setEmail(text.toString());
    }

    @OnTextChanged(R.id.code_password_field)
    void onTextPasswordChanged(CharSequence text, int start, int before, int count) {
        presenter.setPassword(text.toString());
    }

    @OnTextChanged(R.id.code_confirm_password_field)
    void onTextCodeChanged(CharSequence text, int start, int before, int count) {
        presenter.setConfirmPassword(text.toString());
    }

    @OnTextChanged(R.id.code_code_field)
    void onTextConfirmChanged(CharSequence text, int start, int before, int count) {
        presenter.setCode(text.toString());
    }

    @Override
    protected int getBackgroundColorId() {
        return R.color.enter_background;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {

    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        sendAgainButton.setEnabled(false);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        sendAgainButton.setEnabled(true);

    }

    @Override
    public void netError() {
        dialogProvider.showNetError(getContext());
    }

    @Override
    public void serverRegistrationError() {
        dialogProvider.parentRegistrationError(getContext());
    }

    @Override
    public void codeValidationError() {
        codeField.setError(getString(R.string.enter_code_validation_error));
    }

    @Override
    public void emailValidationError() {
        emailField.setError(getString(R.string.enter_email_error));
    }

    @Override
    public void passwordConfirmError() {
        passConfirmField.setError(getString(R.string.enter_confirm_error));
    }

    @Override
    public void passwordValidationError() {
        passwordField.setError(getString(R.string.enter_password_error));
    }

    @Override public void sendAgainLoggedInUserError() {
        dialogProvider.showError(getContext(), getString(R.string.code_send_again_logged_in_user));
    }

    @Override public void sendAgainSuccess() {
        dialogProvider.showSuccess(getContext(), getString(R.string.code_send_again_success));
    }

    public static class Screen extends FragmentScreen {
        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new RegistrationFragment();
        }
    }
}
