package com.ltst.schoolapp.parent.ui.checkout.fragment.share;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;

public interface ShareCodeContract {

    interface Presenter extends BasePresenter {

        void goBack();
        void share();
    }

    interface View extends BaseView<Presenter> {

        void bindView(String code, String header);

    }
    
}
