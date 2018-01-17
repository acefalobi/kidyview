package com.ltst.schoolapp.parent.ui.family.add.check;


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
import com.ltst.schoolapp.parent.ui.family.add.AddMemberScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CheckEmailFragment extends CoreFragment implements CheckEmailContract.View {

    @BindView(R.id.checks_email_done) Button checkButton;
    @BindView(R.id.check_email_text) TextView textView;
    @BindView(R.id.check_email_progress_bar) ProgressBar progressBar;

    @Inject DialogProvider dialogProvider;
    @Inject CheckEmailPresenter presenter;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_check_email;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        AddMemberScope.AddMemberComponent component =
                (AddMemberScope.AddMemberComponent) rootComponent.getComponent();
        component.checkEmailComponent(new CheckEmailScope.CheckEmailModule(this)).inject(this);
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.check_email_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @Override public void enableCheckButton(boolean enable) {
        checkButton.setEnabled(enable);
    }

    @Override public void networkError() {
        dialogProvider.showNetError(getContext());
    }

    @Override public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void bindName(String firstName, String lastName) {
        String format = getString(R.string.check_email_text_format);
        textView.setText(String.format(format, firstName, lastName));

    }

    @Override public void existEmailError() {
        dialogProvider.showError(getContext(), getString(R.string.check_email_exist_error));
    }

    @OnClick(R.id.checks_email_done)
    void onDoneClick() {
        presenter.done();
    }

    @OnTextChanged(R.id.check_email_field)
    void OnEmailTextChanged(CharSequence text, int start, int before, int count) {
        presenter.setEmail(text.toString());
    }

    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new CheckEmailFragment();
        }
    }
}
