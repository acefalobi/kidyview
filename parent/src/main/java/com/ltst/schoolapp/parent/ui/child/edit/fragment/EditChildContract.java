package com.ltst.schoolapp.parent.ui.child.edit.fragment;

import android.content.Intent;
import android.net.Uri;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Child;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public interface EditChildContract {

    interface Presenter extends BasePresenter {

        void setFirstName(String firstName);
        void setLastName(String lastName);
        void setBloodGroup(String bloodGroup);
        void setGenotype(String genotype);
        void setAllergies(String allergies);
        void setAdditionalInfo(String info);
        void setGender(boolean male);
        void done();
        void openCamera();
        void openGallery();
        void onActivityResult(int requestCode,int resultCode, Intent data);
        void goBack();

        void checkWriteExternalPermission();

        void checkCameraPermission();
    }

    interface View extends BaseView<Presenter> {

        void startLoad();
        void stopLoad();
        void bindView(Child child, DatePickerDialog.OnDateSetListener dateSetListener);
        void setBirthDate(Calendar date);
        void setAvatar(Uri avatar);
        void nameValidateError();
        void lastNameValidateError();
        void netError();
        void photoWay();
    }
    
}
