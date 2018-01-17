package com.ltst.schoolapp.teacher.ui.events.add.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.addchild.fragment.SelectableGroup;
import com.ltst.schoolapp.teacher.ui.events.add.AddEventActivityScope.AddEventActivityComponent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEventFragment extends CoreEnterFragment implements AddEventContract.View {

    @Inject
    AddEventPresenter presenter;
    @Inject
    DialogProvider dialogProvider;

    @BindView(R.id.add_event_root) CoordinatorLayout rootCoordinator;
    @BindView(R.id.add_event_time_field) TextView timeField;
    @BindView(R.id.add_event_date_field) TextView dateField;
    @BindView(R.id.add_event_progress_bar) ProgressBar progressBar;
    @BindView(R.id.add_event_text) EditText content;
    @BindView(R.id.add_event_image) ImageView image;
    @BindView(R.id.add_event_file) TextView file;
    @BindView(R.id.add_event_file_container) View fileContainer;
    @BindView(R.id.add_event_camera) TextView camera;
    @BindView(R.id.add_event_pick_file) TextView pickFile;
    @BindView(R.id.add_event_group_field) TextView groupField;
    @BindView(R.id.add_event_app_bar) AppBarLayout appBarLayout;

    @BindColor(R.color.apple_green)
    int appleGreenColor;
    @BindColor((R.color.apple_green_transparent))
    int appleGreenTransparent;

    private Toolbar toolbar;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }


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
        return R.layout.fragment_coordinator_add_event;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        AddEventActivityComponent component = (AddEventActivityComponent) rootComponent
                .getComponent();
        component.addPostComponent(new AddEventScope.AddEventModule(this, this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Drawable cameraImage = ContextCompat.getDrawable(getContext(), R.drawable.ic_camera_24dp);
        cameraImage.mutate();
        cameraImage.setColorFilter(appleGreenColor, PorterDuff.Mode.SRC_ATOP);
        camera.setCompoundDrawablesWithIntrinsicBounds(cameraImage, null, null, null);
        return view;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.add_event_title);
        this.toolbar.inflateMenu(R.menu.menu_done);
    }

    @Override
    public void initToolbar(int icon, View.OnClickListener onClickListener,
                            Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    @Override
    public void bindData(View.OnClickListener onAddPhotoClick,
                         View.OnClickListener onPinFileClick) {
        camera.setOnClickListener(onAddPhotoClick);
        pickFile.setOnClickListener(onPinFileClick);
    }

    @OnClick(R.id.add_event_date_container)
    public void onDateContainerClick(View dateContainer) {
        presenter.openDatePicker();
    }

    @OnClick(R.id.add_event_time_container)
    public void onTimeContainerClick(View timeContainer) {
        presenter.openTimePicker();
    }

    @OnClick(R.id.add_event_group_container)
    public void onGroupContainerClick() {
        presenter.openGroupChooser();
    }

    @Override
    public void chosePhotoWay(DialogProvider.PhotoWayCallBack photoWayCallBack) {
        dialogProvider.showPhotoWay(photoWayCallBack);
    }

    @Override
    public void writePermissionDenied(DialogInterface.OnClickListener onClickListener) {
        dialogProvider.showDeniedWriteExternalPermission();
    }

    @Override
    public void cameraPermissionDenied(DialogInterface.OnClickListener onClickListener) {
        dialogProvider.showDeniedCameraPermsiions();
    }

    @Override
    public String getContent() {
        return content.getText().toString();
    }

    @Override
    public void addPhoto(Uri photoPath, View.OnClickListener onClickListener) {
        Glide.with(getContext())
                .load(photoPath)
                .centerCrop()
                .into(image);
        image.setOnClickListener(onClickListener);
        image.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams params = ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams());
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedFling(rootCoordinator, appBarLayout, null, 0, 1000, true);
        }
    }


    @Override
    public void removePhoto(View view) {
        image.setImageDrawable(null);
        image.setVisibility(View.GONE);
    }

    @Override
    public void addFile(String fileName, View.OnClickListener onClickListener) {
        file.setText(fileName);
        fileContainer.setOnClickListener(onClickListener);
        fileContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeFile(View view) {
        file.setText("");
        fileContainer.setVisibility(View.GONE);
    }

    @Override
    public void setPhotoButtonEnabled(boolean enabled) {
        setEnabledBottomButton(camera, enabled);

    }

    @Override
    public void setFileButtonEnabled(boolean enabled) {
        setEnabledBottomButton(pickFile, enabled);
    }

    private void setEnabledBottomButton(TextView textView, boolean enabled) {
        textView.setEnabled(enabled);
        textView.setClickable(enabled);
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        Drawable icon = compoundDrawables[0];
        if (icon != null) {
            icon.mutate();
            icon.setColorFilter(enabled ? appleGreenColor : appleGreenTransparent, PorterDuff.Mode.SRC_ATOP);
        }
        int textColorRes = enabled ? android.R.color.black : R.color.bluish_grey;
        textView.setTextColor(ContextCompat.getColor(getContext(), textColorRes));
    }

    @Override
    public void showLoading(boolean isShow) {
        progressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showFileManagerError() {
        dialogProvider.showFileManagerError();
    }


    private static final String DATE_FORMAT = "EEE, d/MMM/yyyy";

    @Override
    public void setDate(Calendar dateAndTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateField.setText(dateFormat.format(dateAndTime.getTime()));
    }

    private static final String TIME_FORMAT = "H:mm";

    @Override
    public void setTime(Calendar calendar) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        timeField.setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    public void emptyEventError() {
        dialogProvider.emptyError(getContext());
    }

    @Override
    public void timeError() {
        dialogProvider.eventTimeError(getContext());
    }

    @Override public void setGroupTitle(String title) {
        groupField.setText(title);
    }

    @Override public void showGroupChooser(RecyclerBindableAdapter<SelectableGroup, BindableViewHolder> groupsAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_child_group_select_popup, rootCoordinator, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView groupsRecyclerView = ((RecyclerView) view.findViewById(R.id.group_popup_recycler_view));
        groupsRecyclerView.setLayoutManager(layoutManager);
        groupsRecyclerView.setAdapter(groupsAdapter);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            presenter.changeGroup();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    @Override public void oneGroupMode() {
        ButterKnife.findById(rootCoordinator, R.id.add_event_group_spinner_arrow).setVisibility(View.GONE);
        TextView groupsHeader = ButterKnife.findById(rootCoordinator, R.id.add_event_groups_header);
        groupsHeader.setText(getString(R.string.add_event_one_group_header));
    }

    @Override public void showDateOfEventError() {
        dialogProvider.showError(getContext(), getString(R.string.add_event_date_error));
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new AddEventFragment();
        }
    }
}
