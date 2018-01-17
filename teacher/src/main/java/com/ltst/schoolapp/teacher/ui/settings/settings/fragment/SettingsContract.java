package com.ltst.schoolapp.teacher.ui.settings.settings.fragment;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Group;

import java.util.List;

public interface SettingsContract {

    interface Presenter extends BasePresenter {

        void onEditGroup(long id);

        void onChangePasswordViewClick();

        void onLogOutViewClick();
    }

    interface View extends BaseView<Presenter> {

        void setToolbarNavigationIcon(int icon, android.view.View.OnClickListener onClickListener);

        void bindGroups(List<Group> groups);
    }
}
