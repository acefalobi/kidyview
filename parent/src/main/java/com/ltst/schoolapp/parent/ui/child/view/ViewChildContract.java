package com.ltst.schoolapp.parent.ui.child.view;

import android.content.Intent;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Group;
import com.ltst.schoolapp.parent.data.model.ParentChild;

import java.util.List;

public interface ViewChildContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void openEditChildScreen();

        void openAvatar();

        void familyRequest();

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    interface View extends BaseView<Presenter>{

        void bindData(ParentChild child, boolean canEditChild);

        void setGroups(List<Group> groups);

        void setFamilyAdapter(RecyclerBindableAdapter familyAdapter, boolean canEditChild);
    }
}
