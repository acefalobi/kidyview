package com.ltst.schoolapp.teacher.ui.main.profile;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Profile;

public interface ProfileContract {

    interface Presenter extends BasePresenter {
        void goToEditProfile();

        void goToSettings();

        void sendEmail(String email);
        void openAvatar();

        void openEvents();

        void openSchoolInfo();
    }

    interface View extends BaseView<Presenter> {

        void load();

        void showContent();

        void bindData(Profile profile);

    }
}
