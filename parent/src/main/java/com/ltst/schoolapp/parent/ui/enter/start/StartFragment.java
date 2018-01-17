package com.ltst.schoolapp.parent.ui.enter.start;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.enter.EnterScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartFragment extends CoreFragment implements StartContract.View, View.OnClickListener {

    @Inject
    StartPresenter presenter;

    @BindView(R.id.parent_start_login_button)
    Button loginButton;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_start;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EnterScope.EnterComponent component =
                (EnterScope.EnterComponent) rootComponent.getComponent();
        component.startComponent(new StartScope.StartModule(this)).inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView welcomeTextView = ButterKnife.findById(view, R.id.start_welcome_text);
        spannableWelcomeText(welcomeTextView);
        return view;
    }

    private void spannableWelcomeText(TextView welcomeTextVew) {
        String welcomeText = getString(R.string.start_screen_welcome_text);
        int space = welcomeText.indexOf(StringUtils.SPACE);
        Spannable spannable = new SpannableString(welcomeText);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, space, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeTextVew.setText(spannable);
    }

    @OnClick(R.id.parent_start_login_button)
    void onLoginClick() {
        presenter.goToLogin();
    }

    @OnClick(R.id.start_registration_button)
    void onRegistrationClick() {
        presenter.goToRegistration();
    }

    @Override
    protected int getBackgroundColorId() {
        return R.color.enter_background;
    }


    @Override
    protected void initToolbar(Toolbar toolbar) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.parent_start_login_button) {
            presenter.goToLogin();
        }
    }

    public static final class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new StartFragment();
        }
    }
}
