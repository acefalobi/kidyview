package com.ltst.schoolapp.teacher.ui.child.addmember;

import android.net.Uri;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Member;

public interface AddMemberContract {

    interface Presenter extends BasePresenter {

        void setStatus (String status);

        void setName(String name);

        void setLastName(String lastName);

        void setPhone(String phone);

        void setSecondPhone(String phone);

        void setEmail (String email);

        void done();

        void checkWriteStoragePermission();

        void checkPermissionAndOpenCamera();

        void openGallery();

        void goBack();
    }

    interface View extends BaseView<Presenter> {

        void phoneError();

        void emailError();

        void nameError();

        void lastNameError();

        void secondPhoneError();

        void statusError();

        void chosePhotoWay();

        void setPhoto(Uri photoPath);

        void startLoad();

        void stopLoad();

        void netError();

        void bindExistMember(Member member);

        void bindNewMember(Member member);
    }

}
