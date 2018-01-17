package com.ltst.schoolapp.teacher.ui.editprofile.fragment;

import android.net.Uri;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Profile;
import com.ltst.core.util.validator.ValidateType;

import java.util.Map;

public interface EditProfileContract {

    interface Presenter extends BasePresenter {

        void photoFromCamera();

        void photoFromGallery(Uri data);

        void validateAndUpdate(Map<ValidateType, String> needValidate, String additionalPhone);

        void goBack();

        void checkWriteExternalPermission();

        void checkCameraPermission();

        void openGallery();

        void openCamera();
    }

    interface View extends BaseView<Presenter> {

        void done();

        void setPhoto(Uri bitmap);

        void bindData(Profile profile, boolean needBlockEmail, boolean isAdmin, int screenMode);

        void nameValidateError();

        void lastNameValidateError();

        void personalPhoneValidateError();

        void personalEmailValidateError();

        void schoolTitleValidateError();

        void schoolAddressValidateError();

        void schoolPhoneValidateError();

        void schoolEmailValidateError();

        void noAvatar();

        void load();

        void showContent();

        void setEmailAndBlock(String email);

        void setToolbarTittle(int textId);

        void setToolbarNavigationIcon(int icon, android.view.View.OnClickListener onClickListener);

        void choicePhotoWay();

        void additionalPhoneValidateError();

        void schoolAdditionalPhoneError();
    }
}
