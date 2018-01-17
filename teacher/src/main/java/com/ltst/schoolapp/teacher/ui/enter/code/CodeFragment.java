package com.ltst.schoolapp.teacher.ui.enter.code;

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
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class CodeFragment extends CoreEnterFragment implements CodeContract.View {

    @Inject
    CodePresenter presenter;
    @Inject DialogProvider dialogProvider;
    @BindView(R.id.code_email_field) MaterialEditText emailField;
    @BindView(R.id.code_password_field) MaterialEditText passField;
    @BindView(R.id.code_confirm_password_field) MaterialEditText confirmPassField;
    @BindView(R.id.code_code_field) MaterialEditText codeField;
    @BindView(R.id.code_login) Button loginButton;
    @BindView(R.id.code_progress_bar) ProgressBar progressBar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_code;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EnterScope.EnterComponent component = (EnterScope.EnterComponent) rootComponent.getComponent();
        component.codeComponent(new CodeScope.CodeModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void emailValidateError() {
        emailField.setError(getString(R.string.enter_email_error));
    }

    @Override
    public void emailServerInvalidError() {
        emailField.setError(getString(R.string.enter_email_server_valid_error));
    }

    @Override
    public void passwordError() {
        passField.setError(getString(R.string.enter_password_error));
    }

    @Override
    public void confirmPasswordError() {

        confirmPassField.setError(getString(R.string.enter_confirm_error));
    }

    @Override
    public void codeError() {
        codeField.setError(getString(R.string.code_code_error));
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
    }

    @Override
    public void showNetworkError() {
        dialogProvider.showNetError(getContext());
    }

    @OnFocusChange({R.id.code_email_field, R.id.code_password_field,
                           R.id.code_confirm_password_field, R.id.code_code_field})
    void onFieldFocusChange(MaterialEditText field, boolean hasFocus) {
        if (hasFocus) {
            field.setError(null);
        }
    }

    @OnClick(R.id.code_login)
    void onLoginClick() {
        Map<ValidateType, String> needValidate = new HashMap<>();
        needValidate.put(ValidateType.PERSONAL_EMAIL, emailField.getText().toString());
        needValidate.put(ValidateType.PASSWORD, passField.getText().toString());
        needValidate.put(ValidateType.CONFIRM, confirmPassField.getText().toString());
        needValidate.put(ValidateType.CODE, codeField.getText().toString());
        presenter.validate(needValidate);
    }


    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }

        @Override
        protected Fragment createFragment() {
            return new CodeFragment();
        }
    }
}
