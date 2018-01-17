package com.ltst.schoolapp.teacher.ui.settings.editgroup.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.settings.editgroup.EditGroupActivityScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindView;

public class EditGroupFragment extends CoreEnterFragment implements EditGroupContract.View {

    @Inject
    EditGroupPresenter presenter;

    @Inject
    DialogProvider dialogProvider;

    @BindView(R.id.edit_group_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.edit_group_avatar)
    AvatarView avatarView;
    @BindView(R.id.edit_group_name)
    MaterialEditText title;

    private Toolbar toolbar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getBackgroundColorId() {
        return android.R.color.white;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_edit_group;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        EditGroupActivityScope.EditGroupActivityComponent component = (EditGroupActivityScope.EditGroupActivityComponent) rootComponent.getComponent();
        component.editGroupComponent(new EditGroupScope.EditGroupModule(this, this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initAvatarView();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
            presenter.photoFromCamera();
        } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
            presenter.photoFromGallery(data.getData());
        }
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.edit_group_title);
        this.toolbar.inflateMenu(R.menu.menu_done);
        this.toolbar.setOnMenuItemClickListener(item -> {
            presenter.validateAndUpdate(title.getText().toString());
            return false;
        });
    }

    @Override
    public void setToolbarNavigationIcon(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    private void initAvatarView() {
        avatarView.setClickAvatarCallBack(() -> {
            presenter.checkWriteExternalPermission();
        });
    }

    @Override
    public void setPhoto(Uri photoUri) {
        avatarView.setAvatar(photoUri);
    }

    @Override
    public void bindData(Group group) {
        avatarView.setAvatar(group.getAvatarUrl());
        title.setText(group.getTitle());
        showContent();
    }

    @Override
    public void showContent() {
        avatarView.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
    }


    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(false);
    }

    @Override
    public void showNetworkError() {
        progressBar.setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
        dialogProvider.showNetError(getContext());
    }

    @Override public void showPhotoWay() {
        dialogProvider.showPhotoWay(new DialogProvider.PhotoWayCallBack() {
            @Override public void camera() {
                presenter.checkCameraPermission();
            }

            @Override public void gallery() {
                presenter.openGallery();
            }
        });
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new EditGroupFragment();
        }
    }
}
