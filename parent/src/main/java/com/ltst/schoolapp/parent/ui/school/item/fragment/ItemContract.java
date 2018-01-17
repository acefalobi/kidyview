package com.ltst.schoolapp.parent.ui.school.item.fragment;


import android.support.annotation.DrawableRes;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ItemContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void openDialer();

        void openEmailClient();
    }

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setNames(String names);

        void setAvatar(String avatarUrl, @DrawableRes int holderResId);

        void setAddress(String address, String secondaryText);

        void hideAddressField();

        void setPhone(String phone, String descripton);

        void setAdditionalPhone(String additionalPhone);

        void hideAdditionalPhoneField();

        void setEmail(String email, String description);

    }
}
