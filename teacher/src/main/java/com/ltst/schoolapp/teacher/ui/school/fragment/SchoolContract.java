package com.ltst.schoolapp.teacher.ui.school.fragment;


import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.School;

public interface SchoolContract {
    interface Presenter extends BasePresenter {

        void openEditSchool();

        void goBack();

        void callPhone();

        void callAdditionalPhone();

        void writeEmail();

        void afterEditSchool();
    }

    interface View extends BaseView<Presenter> {

        void bindSchool(School school, boolean isAdmin);
    }
}
