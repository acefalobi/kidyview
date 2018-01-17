package com.ltst.schoolapp.teacher.ui.enter.registration;

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

public class RegistrationFragment extends CoreEnterFragment implements RegistrationContract.View {

    @Inject RegistrationPresenter presenter;

    @Inject DialogProvider dialogProvider;

    @BindView(R.id.registration_email_field)
    MaterialEditText emailField;

    @BindView(R.id.registration_pass_field)
    MaterialEditText passField;

    @BindView(R.id.registration_pass_confirm_field)
    MaterialEditText confirmField;

    @BindView(R.id.edit_profile_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.registration_button)
    Button registrationButton;

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.stop();
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @OnFocusChange({R.id.registration_email_field, R.id.registration_pass_field,
            R.id.registration_pass_confirm_field})
    void onFocusChange(MaterialEditText view, boolean hasFocus) {
        if (hasFocus) {
            view.setError(null);
        }
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_registration;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EnterScope.EnterComponent component = (EnterScope.EnterComponent) rootComponent.getComponent();
        component.registrationComponent(new RegistrationScope.RegistrationModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @OnClick(R.id.registration_button)
    void onRegistrationClick() {
        Map<ValidateType, String> needValidate = new HashMap<>();
        needValidate.put(ValidateType.PERSONAL_EMAIL, emailField.getText().toString());
        needValidate.put(ValidateType.PASSWORD, passField.getText().toString());
        needValidate.put(ValidateType.CONFIRM, confirmField.getText().toString());
        presenter.validate(needValidate);
    }

    @Override
    public void networkError() {
        dialogProvider.showNetError(getContext());
    }

    @Override
    public void errorRegexEmail() {
        emailField.setError(getString(R.string.enter_email_error));
    }

    @Override
    public void errorExistEmail() {
        emailField.setError(getString(R.string.enter_exist_mail_error));
    }

    @Override
    public void errorPassword() {
        passField.setError(getString(R.string.enter_password_error));
    }

    @Override
    public void errorConfirmPassword() {
        confirmField.setError(getString(R.string.enter_confirm_error));
    }

    @Override
    public void showLoad() {
        registrationButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void showContent() {
        registrationButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }


    public static final class Screen extends FragmentScreen {

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
