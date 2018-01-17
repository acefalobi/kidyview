package com.ltst.schoolapp.parent.ui.child.edit.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Child;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.child.ChildActivity;
import com.ltst.schoolapp.parent.ui.child.ChildScope;
import com.ltst.schoolapp.parent.ui.child.edit.EditChildScope;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;

public class EditChildFragment extends CoreFragment implements EditChildContract.View {

    @Inject
    EditChildPresenter presenter;
    @Inject DialogProvider dialogProvider;
    @BindView(R.id.add_child_avatar_view) AvatarView avatarView;
    @BindView(R.id.add_child_name) MaterialEditText nameField;
    @BindView(R.id.add_child_last_name) MaterialEditText lastNameField;
    @BindView(R.id.add_child_gender) RadioGroup genderChooser;
    @BindView(R.id.add_child_birthday_conteainer) ViewGroup birthDayContainer;
    @BindView(R.id.add_child_birthday_field) TextView birthdayField;
    @BindView(R.id.add_child_scroll_view) ScrollView scrollView;
    @BindView(R.id.add_child_progress_bar) ProgressBar progressBar;
    @BindView(R.id.add_child_blood_field) MaterialEditText bloodField;
    @BindView(R.id.add_child_genotype_field) MaterialEditText genotypeField;
    @BindView(R.id.add_child_allergies_field) MaterialEditText allergiesField;
    @BindView(R.id.add_child_additional_field) MaterialEditText informationField;
    private Calendar birthDayCalendar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_edit_child;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        if (rootComponent instanceof EditChildScope.EditChildComponent) {
            EditChildScope.EditChildComponent component = ((EditChildScope.EditChildComponent) rootComponent.getComponent());
            component.editChildComponent(new EditChildFragmentScope.EditChildFragmentModule(this, this)).inject(this);
        } else if (rootComponent instanceof ChildActivity) {
            ChildScope.ChildComponent component = (ChildScope.ChildComponent) rootComponent.getComponent();
            component.editChildComponent(new EditChildFragmentScope.EditChildFragmentModule(this, this))
                    .inject(this);
        }

    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.edit_child_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
        toolbar.inflateMenu(R.menu.menu_done);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                presenter.done();
            }
            return false;
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initAvatarView();
        return view;
    }

    private void initAvatarView() {
        avatarView.setClickAvatarCallBack(() -> presenter.checkWriteExternalPermission());
    }

    @Override public void photoWay() {
        dialogProvider.showPhotoWay(new DialogProvider.PhotoWayCallBack() {
            @Override
            public void camera() {
                presenter.checkCameraPermission();
            }

            @Override
            public void gallery() {
                presenter.openGallery();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void bindView(Child child, DatePickerDialog.OnDateSetListener onDateSetListener) {
        avatarView.setAvatar(child.getAvatarUrl());
        nameField.setText(child.getFirstName());
        lastNameField.setText(child.getLastName());
        String gender = child.getGender();
        genderChooser.check(gender.equals(Child.FEMALE)
                ? R.id.add_child_gender_girl
                : R.id.add_child_gender_boy);
        bloodField.setText(child.getBloodGroup());
        genotypeField.setText(child.getGenotype());
        allergiesField.setText(child.getAllergies());
        informationField.setText(child.getAdditional());
        if (birthDayCalendar == null) {
            birthDayCalendar = Calendar.getInstance();
            birthDayCalendar.set(Calendar.YEAR, birthDayCalendar.get(Calendar.YEAR) - 5);
        }
        birthDayContainer.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(onDateSetListener,
                    birthDayCalendar.get(Calendar.YEAR),
                    birthDayCalendar.get(Calendar.MONTH),
                    birthDayCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setMaxDate(Calendar.getInstance());
            datePickerDialog.showYearPickerFirst(true);
            datePickerDialog.setAccentColor(ContextCompat.getColor(getContext(), R.color.toolbar_color_blue));
            datePickerDialog.show(getActivity().getFragmentManager(), "DatePicker");
        });

    }

    @Override
    public void setAvatar(Uri avatarUri) {
        avatarView.setAvatar(avatarUri);
    }

    @Override
    public void nameValidateError() {
        nameField.setError(getString(R.string.empty_field_error));
    }

    @Override
    public void lastNameValidateError() {
        lastNameField.setError(getString(R.string.empty_field_error));
    }

    @Override
    public void netError() {
        dialogProvider.showNetError(getContext());
    }

    @OnTextChanged(R.id.add_child_name)
    void onNameChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setFirstName(text.toString());
        }
    }

    @OnTextChanged(R.id.add_child_last_name)
    void onLastNameChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setLastName(text.toString());
        }
    }

    @OnCheckedChanged({R.id.add_child_gender_boy, R.id.add_child_gender_girl})
    void onGenderChanged(RadioButton radioButton, boolean isChecked) {
        if (isChecked) {
            presenter.setGender(radioButton.getId() == R.id.add_child_gender_boy);
        }
    }

    @OnTextChanged(R.id.add_child_blood_field)
    void onBloodGroupChanged(CharSequence text, int start, int before, int count) {
        presenter.setBloodGroup(text.toString());
    }

    @OnTextChanged(R.id.add_child_genotype_field)
    void onGenotypeChanged(CharSequence text, int start, int before, int count) {
        presenter.setGenotype(text.toString());
    }

    @OnTextChanged(R.id.add_child_allergies_field)
    void onAllergiesChanged(CharSequence text, int start, int before, int count) {
        presenter.setAllergies(text.toString());
    }

    @OnTextChanged(R.id.add_child_additional_field)
    void onInfoChanged(CharSequence text, int start, int before, int count) {
        presenter.setAdditionalInfo(text.toString());
    }

    @Override
    public void setBirthDate(Calendar calendar) {
        this.birthDayCalendar = calendar;
        SimpleDateFormat dateFormat = new SimpleDateFormat(Child.BIRTHDAY_FORMAT);
        birthdayField.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        birthdayField.setText(dateFormat.format(calendar.getTime()));
    }

    public static final class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new EditChildFragment();
        }
    }
}
