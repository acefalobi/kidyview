package com.ltst.schoolapp.teacher.ui.addchild.fragment;

import android.net.Uri;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;

import java.util.List;

public interface AddChildContract {

    interface Presenter extends BasePresenter {

        void cancel();

        void checkWriteExternalPermission();

        void checkPermissionAndOpenCamera();

        void openAppSettings();

        void openGallery();

        void setName(String name);

        void setLastName(String lastName);

        void setGender(boolean switchEnabled);

        void setBirthDay(int year, int mouth, int day);

        void setBloodGroup(String group);

        void setGenotype(String genotype);

        void setAllergies(String allergies);

        void setAdditional (String additional);

        void done();

        void goBack();

        void selectChildGroup();

        void confirmTempSelectedGroups();

        void cancelTempSelectedGtoups();
    }

    interface View extends BaseView<Presenter>{

        void emptyName();

        void emptyLastName();

        void emptyBirthDate();

        void chosePhotoWay();

        void setPhoto(Uri photoPath);

        void setBirthDate (String date);

        void startLoad();

        void stopLoad();

        void emptyGender();

        void bindChild(Child child);

        void showNetError();

        void setGroups(List<Group> groups);

        void openSelectGroupDialog(RecyclerBindableAdapter adapter);

        void setTitle(String screenTitle);
    }
}
