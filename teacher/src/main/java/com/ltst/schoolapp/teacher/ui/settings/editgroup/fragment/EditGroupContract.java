package com.ltst.schoolapp.teacher.ui.settings.editgroup.fragment;

import android.net.Uri;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Group;

public interface EditGroupContract {

    interface Presenter extends BasePresenter {

        void validateAndUpdate(String title);

        void photoFromCamera();

        void photoFromGallery(Uri data);

        void checkWriteExternalPermission();

        void openCamera();

        void checkCameraPermission();

        void openGallery();
    }

    interface View extends BaseView<Presenter> {

        void setToolbarNavigationIcon(int icon, android.view.View.OnClickListener onClickListener);

        void setPhoto(Uri photoUri);

        void bindData(Group group);

        void showContent();

        void showLoading();

        void showNetworkError();

        void showPhotoWay();
    }
}
