package com.ltst.schoolapp.teacher.ui.child.checkemail;


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
import com.ltst.schoolapp.teacher.ui.child.ChildScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CheckEmailFragment extends CoreFragment implements CheckEmailContract.View {

    @Inject CheckEmailPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.checks_email_done) Button checkButton;
    @BindView(R.id.check_email_text) TextView textView;
    @BindView(R.id.check_email_progress_bar) ProgressBar progressBar;


    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_check_email;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        ChildScope.ChildComponent component = (ChildScope.ChildComponent) rootComponent.getComponent();
        component.checkEmailComponent(new CheckEmailScope.CheckEmailModule(this)).inject(this);
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.check_email_title));
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

        public static final String FRAGMENT_TAG = "CheckEmailFragment.Screen";

        @Override public String getName() {
            return FRAGMENT_TAG;
        }

        @Override protected Fragment createFragment() {
            return new CheckEmailFragment();
        }
    }

}
