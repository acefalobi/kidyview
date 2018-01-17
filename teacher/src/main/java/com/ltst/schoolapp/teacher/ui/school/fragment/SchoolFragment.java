package com.ltst.schoolapp.teacher.ui.school.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.School;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.TwoLineTextViewWithIcon;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.school.SchoolScope;

import javax.inject.Inject;

import butterknife.BindView;

public class SchoolFragment extends CoreFragment implements SchoolContract.View {

    @Inject SchoolPresenter presenter;

    private Toolbar toolbar;

    @BindView(R.id.profile_school_title) TwoLineTextViewWithIcon titleField;
    @BindView(R.id.profile_school_address) TwoLineTextViewWithIcon addressField;
    @BindView(R.id.profile_school_phone) TwoLineTextViewWithIcon phoneField;
    @BindView(R.id.profile_school_email) TwoLineTextViewWithIcon emailField;
    @BindView(R.id.profile_school_additional_phone) TwoLineTextViewWithIcon additionalPhoneField;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_school;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        SchoolScope.SchoolComponent component = (SchoolScope.SchoolComponent) rootComponent.getComponent();
        component.schoolFragmentComponent(new SchoolFragmentScope.SchoolFragmentModule(this)).inject(this);
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.school_info_title));
        toolbar.inflateMenu(R.menu.menu_edit);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                presenter.openEditSchool();
            }
            return false;
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());

    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        phoneField.setOnClickListener(v -> presenter.callPhone());
        emailField.setOnClickListener(v -> presenter.writeEmail());
        additionalPhoneField.setOnClickListener(v -> presenter.callAdditionalPhone());
        return view;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.afterEditSchool();
    }

    @Override public void bindSchool(School school, boolean isAdmin) {
        if (!isAdmin) {
            toolbar.getMenu().findItem(R.id.action_edit).setVisible(false);
        }
        titleField.setMainText(school.getTitle());
        addressField.setMainText(school.getAddress());
        phoneField.setMainText(school.getPhone());
        emailField.setMainText(school.getEmail());
        additionalPhoneField.setMainText(school.getAdditionalPhone());
    }


    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new SchoolFragment();
        }
    }
}
