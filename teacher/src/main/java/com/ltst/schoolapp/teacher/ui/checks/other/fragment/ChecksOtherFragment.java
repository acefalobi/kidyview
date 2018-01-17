package com.ltst.schoolapp.teacher.ui.checks.other.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.checks.other.ChecksOtherActivityScope.ChecksOtherActivityComponent;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindView;

public class ChecksOtherFragment extends CoreEnterFragment implements ChecksOtherContract.View {

    @Inject
    ChecksOtherPresenter presenter;

    @BindView(R.id.checks_other_first_name)
    MaterialEditText firstName;
    @BindView(R.id.checks_other_last_name)
    MaterialEditText lastName;
    @BindView(R.id.checks_other_next)
    Button next;

    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getBackgroundColorId() {
        return R.color.gray_background;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_checks_other;
    }

    @Override
    protected void onCreateComponent(HasSubComponents root) {
        ChecksOtherActivityComponent component = (ChecksOtherActivityComponent) root.getComponent();
        component.checksOtherComponent(new ChecksOtherScope.ChecksOtherModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.check_the_code_title);
    }

    @Override
    public void initToolbar(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    @Override
    public void bindListeners(TextWatcher firstNameWatcher,
                              TextWatcher lastNameWatcher,
                              android.view.View.OnClickListener onNextClick) {
        firstName.addTextChangedListener(firstNameWatcher);
        lastName.addTextChangedListener(lastNameWatcher);
        next.setOnClickListener(onNextClick);
    }

    @Override
    public void setNextEnabled(boolean isEnabled) {
        if (next.isEnabled() == isEnabled) return;
        next.setEnabled(isEnabled);
        next.setClickable(isEnabled);
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ChecksOtherFragment();
        }
    }
}
