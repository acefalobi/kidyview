package com.ltst.schoolapp.teacher.ui.activities.add.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.ui.DialogProvider;

public interface AddPostContract {

    interface Presenter extends BasePresenter {
        void onActivityResult(int requestCode, int resultCode, Intent data);

    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener,
                         Toolbar.OnMenuItemClickListener onMenuItemClickListener);

        void bindData(android.view.View.OnClickListener onSelectPersonListener,
                      RecyclerView.Adapter adapter,
                      android.view.View.OnClickListener onAddPhotoClick);

        void setPersons(String text, @ColorRes int color);

        void setCurrentChildActivity(ChildActivity currentChildActivity);

        void chosePhotoWay(DialogProvider.PhotoWayCallBack photoWayCallBack);

        String getContent();

        void addPhoto(Uri photoPath, android.view.View.OnClickListener onClickListener);

        void removePhoto(android.view.View view);

        void setPhotoButtonEnabled(boolean enabled);

        void showActivityError();

        void showLoading(boolean isShow);

    }
}
