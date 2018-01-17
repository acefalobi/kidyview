package com.ltst.schoolapp.teacher.ui.events.add.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.teacher.ui.addchild.fragment.SelectableGroup;

import java.util.Calendar;

public interface AddEventContract {

    interface Presenter extends BasePresenter {
        void onActivityResult(int requestCode, int resultCode, Intent data);

        void openDatePicker();

        void openTimePicker();

        void openGroupChooser();

        void changeGroup();
    }

    interface View extends BaseView<Presenter> {

        void initToolbar(int icon, android.view.View.OnClickListener onClickListener,
                         Toolbar.OnMenuItemClickListener onMenuItemClickListener);

        void bindData(android.view.View.OnClickListener onAddPhotoClick,
                      android.view.View.OnClickListener onPinFileClick);

        void chosePhotoWay(DialogProvider.PhotoWayCallBack photoWayCallBack);

        void writePermissionDenied(DialogInterface.OnClickListener onClickListener);

        void cameraPermissionDenied(DialogInterface.OnClickListener onClickListener);

        String getContent();

        void addPhoto(Uri photoPath, android.view.View.OnClickListener onClickListener);

        void removePhoto(android.view.View view);

        void addFile(String fileName, android.view.View.OnClickListener onClickListener);

        void removeFile(android.view.View view);

        void setPhotoButtonEnabled(boolean enabled);

        void setFileButtonEnabled(boolean enabled);

        void showLoading(boolean isShow);

        void showFileManagerError();

        //        void showPickedDateAndTime(String dateAndTime);
        void setDate(Calendar calendar);

        void setTime(Calendar calendar);

        void emptyEventError();

        void timeError();

        void setGroupTitle(String title);

        void showGroupChooser(RecyclerBindableAdapter<SelectableGroup, BindableViewHolder> groupsAdapter);

        void oneGroupMode();

        void showDateOfEventError();
    }
}
