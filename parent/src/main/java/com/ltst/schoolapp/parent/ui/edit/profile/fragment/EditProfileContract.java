package com.ltst.schoolapp.parent.ui.edit.profile.fragment;

import android.content.Intent;
import android.net.Uri;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.schoolapp.parent.ui.checkout.fragment.info.ParentProfile;

public interface EditProfileContract {

    interface Presenter extends BasePresenter {

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void setFirstName(String firstName);

        void setLastName(String lastName);

        void setPhone(String phoneNumber);

        void setSecondPhone(String secondPhone);

        void validateAndUpdate();

        void goBack();

        void photoFromCamera();

        void photoFromGallery();
    }

    interface View extends BaseView<Presenter> {

        void cameraPermissionWasDenied();

        void writeExtPermissionWasDenied();

        void setPhoto(Uri bitmap);

        void bindData(ParentProfile parentProfile);

        void nameValidateError();

        void lastNameValidateError();

        void personalPhoneValidateError();
        void secondPhoneValidateError();


        void startLoad();

        void stopLoad();

        void netwrorkError();
    }
}
