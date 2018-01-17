package com.ltst.schoolapp.parent.ui.main.profile;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Profile;

public interface ProfileContract {

    interface Presenter extends BasePresenter {

        void openEditProfile();

        void openAvatarPhoto();

        void logout();

        void openSchoolScreen();
    }

    interface View extends BaseView<Presenter> {

        void setAdapter(RecyclerBindableAdapter adapter);

        void bindProfileData(Profile profile);

        void networkError();

        void startLoad();

        void stopLoad();
    }


}
