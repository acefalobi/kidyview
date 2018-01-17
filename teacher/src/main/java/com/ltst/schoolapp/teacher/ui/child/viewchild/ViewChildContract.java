package com.ltst.schoolapp.teacher.ui.child.viewchild;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Group;

import java.util.List;

public interface ViewChildContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void openFamily();

        void openAvatarPhoto();

        void openAddActivity();
    }

    interface View extends BaseView<Presenter> {

        void setToolbarTitle(String title);

        void setAvatar(String avatarUrl);

        void setFirstName(String name);

        void setLastName(String lastName);

        void setAgeAndGender(String birthday, String gender);

        void hideAgeField();

        void hideBirthdayField();

        void setBirthday(String displayedBirthday);

        void setAllergies(String allergies);

        void setBloodInfo(String group, String genotype);

        void setInfo(String additional);

        void startLoad();

        void stopLoad();

        void networkError();

        void setFamilyPeopleCount(int count, boolean hasNew);

        void setGroups(List<Group> groups);

//        void setGroupsAdapter(RecyclerBindableAdapter groupsAdapter);

    }
}
