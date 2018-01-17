package com.ltst.schoolapp.teacher.ui.addchild.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.addchild.AddChildScope;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class AddChildFragment extends CoreFragment implements AddChildContract.View {

    @Inject
    AddChildPresenter presenter;
    @Inject
    DialogProvider dialogProvider;
    @BindView(R.id.add_child_root_view) ViewGroup rootView;
    @BindView(R.id.add_child_avatar_view) AvatarView avatarView;
    @BindView(R.id.add_child_name) MaterialEditText nameField;
    @BindView(R.id.add_child_last_name) MaterialEditText lastNameField;
    @BindView(R.id.add_child_gender) RadioGroup genderChooser;
    @BindView(R.id.add_child_birthday_field) TextView birthdayField;
    @BindView(R.id.add_child_group_field) TextView groupsField;
    @BindView(R.id.add_child_scroll_view) ScrollView scrollView;
    @BindView(R.id.add_child_progress_bar) ProgressBar progressBar;
    @BindView(R.id.add_child_blood_field) MaterialEditText bloodField;
    @BindView(R.id.add_child_genotype_field) MaterialEditText genotypeField;
    @BindView(R.id.add_child_allergies_field) MaterialEditText allergiesField;
    @BindView(R.id.add_child_additional_field) MaterialEditText informationField;
    private DateListener setDateListener;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_add_child;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        Bundle arguments = getArguments();
        int childId = arguments.getInt(Screen.EDIT_CHILD_ID);
        ((AddChildScope.TempComponent) rootComponent.getComponent())
                .addChildComponent(new AddChildFragmentScope.AddChildFragmentModule(this, this, childId))
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupAvatarView();
        return view;
    }

    private void setupAvatarView() {
        avatarView.setClickAvatarCallBack(new AvatarView.ClickAvatarCallBack() {
            @Override
            public void onAvatarClick() {
                presenter.checkWriteExternalPermission();
            }
        });
    }

    @OnFocusChange({R.id.add_child_name, R.id.add_child_last_name})
    void onNamesFieldsFocusChanged(MaterialEditText editText, boolean isFocus) {
        if (isFocus) {
            editText.setError(null);
        }
    }

    @OnEditorAction(R.id.add_child_last_name)
    boolean onNextLastNameClick(int action) {
        if (action == EditorInfo.IME_ACTION_NEXT) {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(lastNameField.getWindowToken(), 0);
            focusOnView(birthdayField, false);
        }
        return false;
    }

    @OnTextChanged(R.id.add_child_name)
    void onNameChanged(CharSequence text, int start, int before, int count) {
        String name = text.toString();
        if (!StringUtils.isBlank(name)) {
            presenter.setName(name);
        }

    }

    @OnTextChanged(R.id.add_child_last_name)
    void onLastNameChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setLastName(text.toString());
        }

    }

    @OnClick(R.id.add_child_birthday_conteainer)
    void onBirthdayClick(ViewGroup birthdayContainer) {
        if (setDateListener == null) {
            setDateListener = new DateListener();
        }
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(setDateListener,
                year - 5, now.get(Calendar.MONTH), Calendar.DAY_OF_MONTH);
        datePickerDialog.setMaxDate(Calendar.getInstance());
        datePickerDialog.showYearPickerFirst(true);
        datePickerDialog.setAccentColor(ContextCompat.getColor(getContext(), R.color.toolbar_color_blue));
        datePickerDialog.show(getActivity().getFragmentManager(), "DatePicker");
    }

    @OnClick(R.id.add_child_group_container)
    void onGroupsClickListener() {
        presenter.selectChildGroup();
    }

    @OnTextChanged(R.id.add_child_blood_field)
    void OnBloodTextChanged(CharSequence text, int start, int before, int count) {
        presenter.setBloodGroup(text.toString());
    }

    @OnCheckedChanged({R.id.add_child_gender_boy, R.id.add_child_gender_girl})
    void onGenderChanged(RadioButton radioButton, boolean isChecked) {
        if (isChecked) {
            presenter.setGender(radioButton.getId() == R.id.add_child_gender_girl);
        }
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
    void onAdditionalChanged(CharSequence text, int start, int before, int count) {
        presenter.setAdditional(text.toString());
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
        toolbar.inflateMenu(R.menu.menu_done);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                presenter.done();
            }
            return false;
        });
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.goBack();
            }
        });
    }

    @Override
    public void emptyName() {
        focusOnView(nameField, true);
        nameField.setError(getText(R.string.empty_field_error));
    }

    @Override
    public void emptyLastName() {
        focusOnView(lastNameField, true);
        lastNameField.setError(getText(R.string.empty_field_error));
    }

    @Override
    public void emptyBirthDate() {
        focusOnView(birthdayField, true);
        birthdayField.setError(getString(R.string.empty_field_error), null);
        birthdayField.setTextColor(ContextCompat.getColor(getContext(), R.color.error_color));
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void chosePhotoWay() {
        dialogProvider.showPhotoWay(new DialogProvider.PhotoWayCallBack() {
            @Override
            public void camera() {
                presenter.checkPermissionAndOpenCamera();
            }

            @Override
            public void gallery() {
                presenter.openGallery();
            }

        });
    }

    @Override
    public void setPhoto(Uri photoPath) {
        avatarView.setAvatar(photoPath);
    }

    @Override
    public void setBirthDate(String date) {
        birthdayField.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        birthdayField.setText(date);
    }

    @Override
    public void startLoad() {
        toolbar.getMenu().findItem(R.id.action_done).setVisible(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoad() {
        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void emptyGender() {
        dialogProvider.showWarning(getString(R.string.add_child_gender_warning));
        focusOnView(genderChooser, true);
    }

    @Override
    public void bindChild(Child child) {
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
        String birthDay = child.getBirthDay();
        if (!StringUtils.isBlank(birthDay)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Child.SERVER_FORMAT);
            try {
                Date parse = dateFormat.parse(birthDay);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parse);
                dateFormat.applyPattern(Child.BIRTHDAY_FORMAT);
                birthdayField.setText(dateFormat.format(calendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setGroups(List<Group> groups) {
        StringBuilder builder = new StringBuilder();
        if (groups == null || groups.size() == 0) {
            builder.append(StringUtils.EMPTY);
        } else {
            for (int x = 0; x < groups.size(); x++) {
                builder.append(groups.get(x).getTitle());
                if (x != groups.size() - 1) {
                    builder.append(StringUtils.SPACE)
                            .append(StringUtils.COMMA);
                }
            }
        }

        groupsField.setText(builder.toString());

    }

    @Override public void openSelectGroupDialog(RecyclerBindableAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_child_group_select_popup, rootView, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.group_popup_recycler_view));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            presenter.confirmTempSelectedGroups();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            presenter.cancelTempSelectedGtoups();
        });
        builder.create().show();
    }

    @Override public void setTitle(String screenTitle) {
        toolbar.setTitle(screenTitle);
    }

    @Override
    public void showNetError() {
        dialogProvider.showNetError(getContext());
    }

    private final void focusOnView(final View view, boolean withSmooth) {
        scrollView.post(() -> {
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (AddChildFragment.this.getActivity().getTheme()
                    .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue
                        .complexToDimensionPixelSize(tv.data, AddChildFragment.this.getResources().getDisplayMetrics());
            }
            if (withSmooth) {
                scrollView.smoothScrollTo(0, view.getBottom() - 2 * actionBarHeight);
            } else {
                scrollView.scrollTo(0, view.getBottom() - 2 * actionBarHeight);
            }

        });
    }

    public static class Screen extends BottomNavigationFragmentScreen {

        public static final int ADD_CHILD = 0;
        public static final int EDIT_CHILD = 1;

        @IntDef({ADD_CHILD, EDIT_CHILD})
        @Retention(RetentionPolicy.SOURCE)
        @interface ScreenMode {

        }

        public static final String EDIT_CHILD_ID = "AddChildScreen.Edit";
        private int childId;

        public Screen(int childId) {
            this.childId = childId;
        }

        public Screen() {
        }

        @Override
        protected void onAddArguments(Bundle arguments) {
            arguments.putInt(EDIT_CHILD_ID, childId);
            super.onAddArguments(arguments);
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new AddChildFragment();
        }

        @Override
        public int unselectedIconId() {
            return R.drawable.ic_add_alert_white_24dp;
        }

        @Override
        public int selectedIconId() {
            return R.drawable.ic_add_alert_white_24dp;
        }
    }

    private class DateListener implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            presenter.setBirthDay(year, monthOfYear, dayOfMonth);
        }
    }
}
