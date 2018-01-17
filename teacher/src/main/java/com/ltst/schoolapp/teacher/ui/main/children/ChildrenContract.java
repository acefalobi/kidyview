package com.ltst.schoolapp.teacher.ui.main.children;

import com.danil.recyclerbindableadapter.library.FilterBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Child;

public interface ChildrenContract {

    interface View extends BaseView<Presenter> {

        void showLoad();

        void showEmpty();

        void showList(Integer itemPosition);

        void setAdapter(FilterBindableAdapter adapter);

        void deleteChildWarning(String childName);

        void networkError();
    }

    interface Presenter extends BasePresenter {

        void goToAddChildScreen();

        void setSearch(String query);

        void onListItemClick(Child position);

        void deleteSelectedChild();

    }
}
