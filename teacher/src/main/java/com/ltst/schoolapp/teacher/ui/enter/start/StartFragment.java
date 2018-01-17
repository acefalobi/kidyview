package com.ltst.schoolapp.teacher.ui.enter.start;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.enter.EnterScope;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartFragment extends CoreEnterFragment implements StartContract.View {

    @Inject
    StartPresenter presenter;

    @Inject
    DialogProvider dialogProvider;


    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_start;
    }

    @Override
    protected void onCreateComponent(HasSubComponents hasSubComponents) {
        EnterScope.EnterComponent enterComponent =
                (EnterScope.EnterComponent) hasSubComponents.getComponent();
        enterComponent.startComponent(new StartScope.StartModule(this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView welcomeTextView = ButterKnife.findById(view,R.id.start_welcome_text_first);
        spannableWelcomeText(welcomeTextView);
        return view;
    }

    protected void spannableWelcomeText(TextView welcomeTextView) {
        String welcomeText = getString(R.string.start_screen_welcome_text_first);
        int space = welcomeText.indexOf(StringUtils.SPACE);
        Spannable spannable = new SpannableString(welcomeText);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, space, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeTextView.setText(spannable);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @OnClick(R.id.start_code)
    void onCodeButtonClick() {
        goToCodeScreen();
    }

    @OnClick(R.id.start_login)
    void onLoginButtonClick() {
        goToLoginScreen();
    }

    @OnClick(R.id.start_registration)
    void onRegistrationClick() {
        goToRegistrationScreen();
    }

    @Override
    public void goToCodeScreen() {
        presenter.openCodeScreen();
    }

    @Override
    public void goToRegistrationScreen() {
        presenter.openRegistrationScreen();
    }

    @Override
    public void goToLoginScreen() {
        presenter.openLoginScreen();
    }

    @Override
    public void showLogoutFromServerPopup() {
        dialogProvider.showLogoutFromServerPopup(getContext());
    }


    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }

        @Override
        protected Fragment createFragment() {
            return new StartFragment();
        }
    }
}
