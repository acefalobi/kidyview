package com.ltst.schoolapp.parent.ui.select.dialog.fragment;


import com.danil.recyclerbindableadapter.library.FilterBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.ui.adapter.dialog.DialogItem;
import com.ltst.core.ui.holder.DialogMemberViewHolder;

public interface SelectMemberContract {

    interface Presenter extends BasePresenter {

        void goBack();

        void searchText(String string);

        void done();
    }

    interface View extends BaseView<Presenter> {

        void setAdapter(FilterBindableAdapter<DialogItem, DialogMemberViewHolder> memberAdapter);

        void addDoneMenuItem();
    }
}
