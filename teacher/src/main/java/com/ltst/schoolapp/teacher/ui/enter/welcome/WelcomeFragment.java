package com.ltst.schoolapp.teacher.ui.enter.welcome;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.enter.EnterScope;

import javax.inject.Inject;

import butterknife.OnClick;

public class WelcomeFragment extends CoreEnterFragment implements WelcomeContract.View {

    @Inject WelcomePresenter presenter;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_welcome;
    }

    @OnClick(R.id.welcome_continue_button)
    void onContinueClick(){
        presenter.nextScreen();
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EnterScope.EnterComponent component = (EnterScope.EnterComponent) rootComponent.getComponent();
        component.welcomeComponent(new WelcomeScope.WelcomeModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new WelcomeFragment();
        }
    }


}
