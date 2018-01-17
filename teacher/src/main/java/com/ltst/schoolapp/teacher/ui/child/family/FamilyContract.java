package com.ltst.schoolapp.teacher.ui.child.family;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Member;

public interface FamilyContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void openAddMember();

        void changeStatusForMember();

        void dismissChangeStatus();
    }

    interface View extends BaseView<Presenter> {

        void setAdapter(RecyclerBindableAdapter adapter);

        void startLoad();

        void stopLoad();

        void showChangeStatusPopup(Member member, RecyclerBindableAdapter adapter);
    }
}
