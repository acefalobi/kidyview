package com.ltst.schoolapp.parent.ui.edit.profile.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.checkout.fragment.info.ParentProfile;
import com.ltst.schoolapp.parent.ui.edit.profile.EditProfileScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class EditProfileFragment extends CoreFragment implements EditProfileContract.View {

    @Inject
    EditProfilePresenter presenter;
    @Inject
    DialogProvider dialogProvider;
    @BindView(R.id.add_member_avatar_view) AvatarView avatarView;
    @BindView(R.id.add_member_name) MaterialEditText nameField;
    @BindView(R.id.add_member_last_name) MaterialEditText lastNameField;
    @BindView(R.id.add_member_phone) MaterialEditText phoneField;
    @BindView(R.id.add_member_secod_phone) MaterialEditText secondPhoneField;
    @BindView(R.id.add_member_proress_bar) ProgressBar progressBar;
    @BindString(R.string.empty_field_error)
    String emptyFieldError;

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
        EditProfileScope.EditProfileComponent component =
                (EditProfileScope.EditProfileComponent) rootComponent.getComponent();
        component.editProfileComponent(new EditProfileFragmentScope.EditProfileFragmentModule(this, this))
                .inject(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        avatarView.setClickAvatarCallBack(() -> {
            dialogProvider.showPhotoWay(new DialogProvider.PhotoWayCallBack() {
                @Override
                public void camera() {
                    presenter.photoFromCamera();
                }

                @Override
                public void gallery() {
                    presenter.photoFromGallery();
                }
            });
        });
        return view;
    }

    @OnFocusChange({R.id.add_member_name, R.id.add_member_last_name,
            R.id.add_member_phone, R.id.add_member_secod_phone})
    void onFieldsFocusChanged(MaterialEditText editText, boolean isFocus) {
        if (isFocus) {
            editText.setError(null);
        }
    }

    @OnTextChanged(R.id.add_member_name)
    void onNameChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setFirstName(text.toString());
        }
    }

    @OnTextChanged(R.id.add_member_last_name)
    void onLastNameChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setLastName(text.toString());
        }
    }

    @OnTextChanged(R.id.add_member_phone)
    void onPhoneChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setPhone(text.toString());
        }
    }

    @OnTextChanged(R.id.add_member_secod_phone)
    void onSecondPhoneChanged(CharSequence text, int start, int before, int count) {
            presenter.setSecondPhone(text.toString());
    }

    @Override

    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.edit_profile_title);
        toolbar.inflateMenu(R.menu.menu_done);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                presenter.validateAndUpdate();
            }
            return false;
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            presenter.goBack();
        });
    }

    @Override
    public void cameraPermissionWasDenied() {
        dialogProvider.showDeniedCameraPermsiions();
    }

    @Override
    public void writeExtPermissionWasDenied() {
        dialogProvider.showDeniedWriteExternalPermission();
    }

    @Override
    public void setPhoto(Uri bitmap) {
        avatarView.clearAvatar();
        avatarView.setAvatar(bitmap);
    }

    @Override
    public void bindData(ParentProfile parentProfile) {
        Profile profile = parentProfile.getProfile();
        nameField.setText(profile.getFirstName());
        lastNameField.setText(profile.getLastName());
        if (!StringUtils.isBlank(profile.getAvatarUrl())) {
            setPhoto(Uri.parse(profile.getAvatarUrl()));
        }
        phoneField.setText(profile.getPhone());
        secondPhoneField.setText(profile.getAdditionalPhone());
    }

    @Override
    public void nameValidateError() {
        nameField.setError(emptyFieldError);

    }



    @Override
    public void lastNameValidateError() {
        lastNameField.setError(emptyFieldError);
    }

    @Override
    public void personalPhoneValidateError() {
        phoneField.setError(getString(R.string.phone_not_valid));
    }

    @Override
    public void secondPhoneValidateError() {
        secondPhoneField.setError(getString(R.string.phone_not_valid));
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
    public void netwrorkError() {
        dialogProvider.showNetError(getContext());
    }

    public static final class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new EditProfileFragment();
        }
    }
}
