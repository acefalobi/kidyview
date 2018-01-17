package com.ltst.schoolapp.parent.ui.checkout.fragment.info;

import android.content.Intent;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface InfoContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void validateFields();

        void selectChild();

        void setRelativeFirstName(String firstName);

        void setRelativeLastName(String lastName);

        void openTimePicker();

        void setRelativeStatus(String string);

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    interface View extends BaseView<Presenter> {

        void startLoad();

        void stopLoad();

        void setObjectName(String childName);

        void setTime(String time);

        void networkError();

        void emptyChildIdError();

        void statusValidateError();

        void firstNameValidateError();

        void lastNameValidateError();

        void timeEmptyError();

        void setEmptyChildField();

        void emptyChildListMode();

        void oneChildMode(String childName);

        void manyChildrenMode();
    }


}
