package com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.settings.changepassword.ChangePasswordActivityScope.ChangePasswordActivityComponent;
import com.ltst.schoolapp.teacher.ui.settings.changepassword.fragment.ChangePasswordScope.ChangePasswordModule;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class ChangePasswordFragment extends CoreFragment implements ChangePasswordContract.View {

    @Inject
    ChangePasswordPresenter presenter;

    @BindString(R.string.edit_profile_field_empty_error)
    String emptyFieldError;

    @BindView(R.id.change_password_old)
    MaterialEditText oldPassword;
    @BindView(R.id.change_password_new)
    MaterialEditText newPassword;
    @BindView(R.id.change_password_repeat)
    MaterialEditText repeatPassword;

    private Toolbar toolbar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        ChangePasswordActivityComponent component
                = (ChangePasswordActivityComponent) rootComponent.getComponent();
        ChangePasswordModule module = new ChangePasswordModule(this);
        component.changePassword(module).inject(this);
    }


    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.inflateMenu(R.menu.menu_done);
        this.toolbar.setTitle(R.string.change_password_title);
        this.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                done();
            }
            return false;
        });
    }

    @Override
    public void bindData(Profile profile) {
    }

    private void done() {
        Map<ValidateType, String> needValidate = new HashMap<>();
        String oldPasswordText = oldPassword.getText().toString();
        String newPasswordText = newPassword.getText().toString();
        String repeatPasswordText = repeatPassword.getText().toString();
        needValidate.put(ValidateType.OLD_PASSWORD, oldPasswordText);
        needValidate.put(ValidateType.PASSWORD, newPasswordText);
        needValidate.put(ValidateType.CONFIRM, repeatPasswordText);
        presenter.validateAndUpdate(needValidate);
    }

//    @Override
//    public void load() {
//        progressBar.setVisibility(View.VISIBLE);
//        toolbar.getMenu().findItem(R.id.action_done).setVisible(false);
//    }

//    @Override
//    public void showContent() {
//        progressBar.setVisibility(View.GONE);
//        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
//    }


    @Override
    public void setToolbarNavigationIcon(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    @OnClick(R.id.change_password_forgot)
    void onForgotPasswordClick() {
        presenter.openForgotPassword();
    }

    @Override
    public void errorPassword() {
        newPassword.setError(getString(R.string.enter_password_error));
    }

    @Override
    public void errorConfirmPassword() {
        repeatPassword.setError(getString(R.string.enter_confirm_error));
    }

    @Override
    public void errorOldPassword() {
        oldPassword.setError(getString(R.string.change_password_old_incorrect));
    }


    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ChangePasswordFragment();
        }
    }
}
