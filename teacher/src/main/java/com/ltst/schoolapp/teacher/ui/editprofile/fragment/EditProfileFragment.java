package com.ltst.schoolapp.teacher.ui.editprofile.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.editprofile.EditProfileActivityScope.EditProfileActivityComponent;
import com.ltst.schoolapp.teacher.ui.enter.EnterScope.EnterComponent;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;

import static com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileScope.EditProfileModule;

public class EditProfileFragment extends CoreEnterFragment implements EditProfileContract.View {

    private static final int CAMERA_REQUEST_CODE = 2288;
    private static final int GALLERY_REQUEST_CODE = 3377;

    @Inject EditProfilePresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindString(R.string.edit_profile_field_empty_error) String emptyFieldError;
    @BindView(R.id.edit_profile_avatar_view) AvatarView avatarView;
    @BindView(R.id.edit_profile_name) MaterialEditText nameField;
    @BindView(R.id.edit_profile_last_name) MaterialEditText lastNameField;
    @BindView(R.id.edit_profile_personal_phone) MaterialEditText personalPhoneField;
    @BindView(R.id.edit_profile_secod_phone) MaterialEditText secondPersonalPhoneField;
    @BindView(R.id.edit_profile_personal_email) MaterialEditText personalEmailField;
    @BindView(R.id.edit_profile_school_title) MaterialEditText schoolTitleField;
    @BindView(R.id.edit_profile_school_address) MaterialEditText schoolAddressField;
    @BindView(R.id.edit_profile_school_phone) MaterialEditText schoolPhoneFiled;
    @BindView(R.id.edit_profile_school_additional_phone) MaterialEditText schoolAdditionalPhoneField;
    @BindView(R.id.edit_profile_school_email) MaterialEditText schoolEmailField;
    @BindView(R.id.edit_profile_progress_bar) ProgressBar progressBar;
    @BindView(R.id.edit_profile_content) ScrollView scrollView;

    private Toolbar toolbar;
    private int toolbarTittle;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        int screenMode = getArguments().getInt(SCREEN_MODE);
        if (rootComponent.getComponent() instanceof EnterComponent) {
            EnterComponent component = (EnterComponent) rootComponent.getComponent();
            EditProfileModule module = new EditProfileModule(this, this, EditProfileModule.FROM_ENTER, screenMode);
            component.profileComponent(module).inject(this);
        } else if (rootComponent.getComponent() instanceof EditProfileActivityComponent) {
            EditProfileActivityComponent component = (EditProfileActivityComponent) rootComponent.getComponent();
            EditProfileModule module = new EditProfileModule(this, this, EditProfileModule.FROM_PROFILE, screenMode);
            component.profileComponent(module).inject(this);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initAvatarView();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
            presenter.photoFromCamera();
        } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
            presenter.photoFromGallery(data.getData());
        }
    }

    private void initAvatarView() {
        avatarView.setClickAvatarCallBack(() -> presenter.checkWriteExternalPermission());
    }

    @Override
    public void choicePhotoWay() {
        dialogProvider.showPhotoWay(new DialogProvider.PhotoWayCallBack() {
            @Override public void camera() {
                presenter.checkCameraPermission();
            }

            @Override public void gallery() {
                presenter.openGallery();
            }
        });
    }


    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.inflateMenu(R.menu.menu_done);
        this.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                done();
            }
            return false;
        });
    }

    @Override
    public void bindData(Profile profile, boolean needBlockEmailField, boolean isAdmin, int screenMode) {
        nameField.setText(profile.getFirstName());
        lastNameField.setText(profile.getLastName());
        personalPhoneField.setText(profile.getPhone());
        secondPersonalPhoneField.setText(profile.getAdditionalPhone());
        if (needBlockEmailField) {
            setEmailAndBlock(profile.getEmail());
        } else {
            personalEmailField.setText(profile.getEmail());
        }

        avatarView.setAvatar(profile.getAvatarUrl());
        if (profile.getSchool() == null) return;
        schoolTitleField.setText(profile.getSchool().getTitle());
        schoolAddressField.setText(profile.getSchool().getAddress());
        schoolPhoneFiled.setText(profile.getSchool().getPhone());
        schoolAdditionalPhoneField.setText(profile.getSchool().getAdditionalPhone());
        schoolEmailField.setText(profile.getSchool().getEmail());
        if (!isAdmin) {
            schoolTitleField.setFocusable(false);
            schoolAddressField.setFocusable(false);
            schoolPhoneFiled.setFocusable(false);
            schoolEmailField.setFocusable(false);
            schoolAdditionalPhoneField.setFocusable(false);
        }
        switch (screenMode) {
            case DEFAULT:
                break;
            case PROFILE:
                ButterKnife.findById(getView(), R.id.edit_profile_school_separator).setVisibility(View.GONE);
                ButterKnife.findById(getView(), R.id.edit_profile_school_info_title).setVisibility(View.GONE);
                schoolTitleField.setVisibility(View.GONE);
                schoolAddressField.setVisibility(View.GONE);
                schoolPhoneFiled.setVisibility(View.GONE);
                schoolEmailField.setVisibility(View.GONE);
                schoolAdditionalPhoneField.setVisibility(View.GONE);

                break;
            case SCHOOL:
                avatarView.setVisibility(View.GONE);
                nameField.setVisibility(View.GONE);
                lastNameField.setVisibility(View.GONE);
                personalEmailField.setVisibility(View.GONE);
                personalPhoneField.setVisibility(View.GONE);
                secondPersonalPhoneField.setVisibility(View.GONE);
                ButterKnife.findById(getView(), R.id.edit_profile_school_separator).setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected int getBackgroundColorId() {
        return android.R.color.white;
    }


    @Override
    public void done() {
        Map<ValidateType, String> needValidate = new HashMap<>();
        String name = nameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String personalPhone = personalPhoneField.getText().toString();
        String secondPersonalPhone = secondPersonalPhoneField.getText().toString();
        String personalEmail = personalEmailField.getText().toString();
        String schoolTitle = schoolTitleField.getText().toString();
        String schoolAddress = schoolAddressField.getText().toString();
        String schoolPhone = schoolPhoneFiled.getText().toString();
        String schoolEmail = schoolEmailField.getText().toString();
        String schoolAdditionalPhone = schoolAdditionalPhoneField.getText().toString();
        needValidate.put(ValidateType.NAME, name);
        needValidate.put(ValidateType.LAST_NAME, lastName);
        needValidate.put(ValidateType.LAST_NAME, lastName);
        needValidate.put(ValidateType.PERSONAL_PHONE, personalPhone);
        needValidate.put(ValidateType.PERSONAL_EMAIL, personalEmail);
        needValidate.put(ValidateType.SCHOOL_TITLE, schoolTitle);
        needValidate.put(ValidateType.SCHOOL_ADDRESS, schoolAddress);
        needValidate.put(ValidateType.SCHOOL_PHONE, schoolPhone);
        needValidate.put(ValidateType.SCHOOL_PHONE, schoolPhone);
        if (!StringUtils.isBlank(schoolAdditionalPhone)) {
            needValidate.put(ValidateType.SCHOOL_ADDITIONAL_PHONE, schoolAdditionalPhone);
        }
        needValidate.put(ValidateType.SCHOOL_EMAIL, schoolEmail);
        presenter.validateAndUpdate(needValidate, secondPersonalPhone);
    }

    @OnFocusChange({R.id.edit_profile_name,
                           R.id.edit_profile_last_name,
                           R.id.edit_profile_personal_phone,
                           R.id.edit_profile_personal_email,
                           R.id.edit_profile_school_title,
                           R.id.edit_profile_school_address,
                           R.id.edit_profile_school_phone,
                           R.id.edit_profile_school_email,
                           R.id.edit_profile_school_additional_phone})
    void onFiledsFocusChanged(MaterialEditText editText, boolean focused) {
        if (focused) {
            editText.setError(null);
        }
    }

    @Override
    public void setPhoto(Uri photoUri) {
        focusOnView(avatarView);
        avatarView.setAvatar(photoUri);
    }

    @Override
    public void nameValidateError() {
        focusOnView(nameField);
        nameField.setError(emptyFieldError);
    }

    @Override
    public void lastNameValidateError() {
        focusOnView(lastNameField);
        lastNameField.setError(emptyFieldError);
    }

    @Override
    public void personalPhoneValidateError() {
        focusOnView(personalPhoneField);
        personalPhoneField.setError(emptyFieldError);
    }

    @Override public void additionalPhoneValidateError() {
        focusOnView(secondPersonalPhoneField);
        secondPersonalPhoneField.setError(getString(R.string.edit_profile_additional_phone_length_error));
    }


    @Override
    public void personalEmailValidateError() {
        focusOnView(personalEmailField);
        personalEmailField.setError(getString(R.string.edit_profile_email_error));
    }

    @Override
    public void schoolTitleValidateError() {
        focusOnView(schoolTitleField);
        schoolTitleField.setError(emptyFieldError);
    }

    @Override
    public void schoolAddressValidateError() {
        focusOnView(schoolAddressField);
        schoolAddressField.setError(emptyFieldError);
    }

    @Override
    public void schoolPhoneValidateError() {
        focusOnView(schoolPhoneFiled);
        schoolPhoneFiled.setError(emptyFieldError);
    }

    @Override public void schoolAdditionalPhoneError() {
        focusOnView(schoolAdditionalPhoneField);
        schoolAdditionalPhoneField.setError(getString(R.string.edit_profile_additional_phone_length_error));
    }

    @Override
    public void schoolEmailValidateError() {
        focusOnView(schoolEmailField);
        schoolEmailField.setError(getString(R.string.edit_profile_email_error));
    }

    private final void focusOnView(final View view) {
        scrollView.post(() -> {
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }
            scrollView.smoothScrollTo(0, view.getTop() - 2 * actionBarHeight);
        });
    }

    @Override
    public void noAvatar() {
        focusOnView(avatarView);
        avatarView.emptyAvatarError();
    }

    @Override
    public void load() {
        progressBar.setVisibility(View.VISIBLE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(false);
    }

    @Override
    public void showContent() {
        progressBar.setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
    }

    @Override
    public void setEmailAndBlock(String email) {
        personalEmailField.setText(email);
        personalEmailField.setFocusable(false);
    }

    @Override
    public void setToolbarTittle(int textId) {
        toolbar.setTitle(textId);
    }

    @Override
    public void setToolbarNavigationIcon(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    public static class Screen extends FragmentScreen {

        private final int screenMode;

        public Screen(@ScreenMode int screenMode) {
            this.screenMode = screenMode;
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new EditProfileFragment();
        }

        @Override protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
            arguments.putInt(SCREEN_MODE, screenMode);
        }
    }

    public static final String SCREEN_MODE = "EditProfileScreen.mode";

    @IntDef({DEFAULT, PROFILE, SCHOOL})
    public @interface ScreenMode {
    }

    public static final int DEFAULT = 0;
    public static final int PROFILE = 1;
    public static final int SCHOOL = 2;
}
