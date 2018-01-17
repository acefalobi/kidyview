package com.ltst.schoolapp.teacher.ui.enter.login;

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
import com.ltst.core.util.validator.ValidateType;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.enter.EnterScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class LoginFragment extends CoreEnterFragment implements LoginContract.View {

    @Inject LoginPresenter presenter;

    @BindView(R.id.login_email_field)
    MaterialEditText emailField;

    @BindView(R.id.login_password_field)
    MaterialEditText passwordField;

    @BindView(R.id.edit_profile_progress_bar)
    ProgressBar progressBar;

    @Inject DialogProvider dialogProvider;

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (BuildConfig.DEBUG) {
//            emailField.setText("test+3@test.ru");
//            passwordField.setText("1Q2w3e4r");
//        }
//    }

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
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void errorEmail() {
        emailField.setError(getString(R.string.enter_email_error));
    }

    @Override
    public void setEmptyPasswordError() {
        passwordField.setError(getString(R.string.login_empty_password));
    }

    @Override
    public void loginError() {
        dialogProvider.showLoginError(getContext());
    }

    @Override public void userNotFoundError() {
        dialogProvider.userNotFoundError(getContext());
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
        ((Button) getView().findViewById(R.id.login_button)).setEnabled(true);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        ((Button) getView().findViewById(R.id.login_button)).setEnabled(false);
    }

    @Override
    public void showNetError() {
        dialogProvider.showNetError(getContext());
    }

    @OnFocusChange(R.id.login_email_field)
    void onEmailFieldFocusChanged(MaterialEditText emailField, boolean hasFocus) {
        if (hasFocus) {
            emailField.setError(null);
        }
    }


    @OnClick(R.id.login_button)
    void onLoginClick() {
        HashMap<ValidateType, String> needValidate = new HashMap<>();
        needValidate.put(ValidateType.PERSONAL_EMAIL, emailField.getText().toString());
        presenter.validate(needValidate, passwordField.getText().toString());
    }

    @OnClick(R.id.login_forgot_password)
    void onForgotPasswordClick() {
        presenter.forgotPassword();
    }


    public static final class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }

        @Override
        protected Fragment createFragment() {
            return new LoginFragment();
        }
    }
}
