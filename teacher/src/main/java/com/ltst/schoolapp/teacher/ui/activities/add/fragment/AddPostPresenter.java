package com.ltst.schoolapp.teacher.ui.activities.add.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.core.util.PhotoRotator;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.activities.add.AddPostActivity;
import com.ltst.schoolapp.teacher.ui.activities.select.person.SelectPersonActivity;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ltst.schoolapp.teacher.ui.activities.select.person.fragment.SelectPersonPresenter.KEY_CHECKED_ITEMS;

public class AddPostPresenter implements AddPostContract.Presenter,
        ChildActivityViewHolder.ChildActivityListener, DialogProvider.PhotoWayCallBack, Toolbar.OnMenuItemClickListener {

    private static final int SELECT_PERSON_RC = 1352;
    public static final int MAX_COUNT_PHOTOS = 3;
    public static final String KEY_ADDED_POST = "AddPostPresenter.added.post";
    private final AddPostContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final DataService dataService;
    private final TeacherApplication application;
    private final GalleryPictureLoader galleryPictureLoader;
    private final ApplicationSwitcher applicationSwitcher;
    private final TeacherApplication appContext;
    private final Bundle screenParams;
    private final PermissionsHandler permissionsHandler;
    private CompositeSubscription subscriptions;
    private ArrayList<SelectPersonModel> selectedChildren = new ArrayList<>();
    private SimpleBindableAdapter<ChildActivity> childActivitiesAdapter;
    private ChildActivity currentActivity;
    private boolean isAdding = false;
    private Uri currentPhotoPath;
    private List<String> photos = new ArrayList<>();
    private File fileForDelete;
    private long groupId;

    @Inject
    public AddPostPresenter(AddPostContract.View view,
                            ActivityScreenSwitcher screenSwitcher,
                            DataService dataService,
                            TeacherApplication application,
                            PermissionsHandler permissionsHandler,
                            GalleryPictureLoader galleryPictureLoader,
                            ApplicationSwitcher applicationSwitcher,
                            TeacherApplication appContext,
                            Bundle screenParams) {
        this.permissionsHandler = permissionsHandler;
        this.view = view;
        this.activitySwitcher = screenSwitcher;
        this.dataService = dataService;
        this.application = application;
        this.galleryPictureLoader = galleryPictureLoader;
        this.applicationSwitcher = applicationSwitcher;
        this.appContext = appContext;
        this.screenParams = screenParams;
        this.childActivitiesAdapter = new SimpleBindableAdapter<>(
                R.layout.fragment_add_post_child_activity,
                ChildActivityViewHolder.class);
        this.childActivitiesAdapter.setActionListener(this);
        Child child = screenParams.getParcelable(AddPostActivity.Screen.ADD_ACTIVITY_FOR_CHILD_KEY);
        if (child != null) {
            selectedChildren.add(SelectPersonModel.fromChild(child));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SELECT_PERSON_RC) {
            if (data == null || !data.hasExtra(KEY_CHECKED_ITEMS)) return;
            selectedChildren = data.getParcelableArrayListExtra(KEY_CHECKED_ITEMS);
        } else if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
            addPhoto();
        } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
            fileForDelete = galleryPictureLoader.getFileWithRotate(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            currentPhotoPath = Uri.fromFile(fileForDelete);
            addPhoto();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  FIRST START  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        subscriptions.add(dataService.getChildActivities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bindActivities, Throwable::printStackTrace));
        subscriptions.add(dataService.getSelectedGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(selectedGroupSubscribe));
    }


    private Action1<List<ChildActivity>> bindActivities = childActivities -> {
        childActivitiesAdapter.addAll(childActivities);
    };

    private Action1<Group> selectedGroupSubscribe = group -> {
        groupId = group.getId();
    };

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  START  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void start() {
        subscriptions = new CompositeSubscription();
        initToolbar();
        view.bindData(onSelectPersonListener(),
                childActivitiesAdapter,
                onPhotoClickListener());
        view.setPersons(getTextFromChild(), getTextColor());
    }

    private View.OnClickListener onSelectPersonListener() {
        return v -> {
            ActivityScreen screen = new SelectPersonActivity.Screen(groupId, selectedChildren);
            activitySwitcher.startForResult(screen, SELECT_PERSON_RC);
        };
    }

    private View.OnClickListener onPhotoClickListener() {
        return v -> view.chosePhotoWay(this);
    }

    private String getTextFromChild() {
        if (selectedChildren == null || selectedChildren.size() == 0) {
            return application.getString(R.string.add_post_select_person);
        }
        StringBuilder builder = new StringBuilder();
        for (SelectPersonModel model : selectedChildren) {
            if (builder.length() != 0) {
                builder.append(StringUtils.COMMA);
                builder.append(StringUtils.SPACE);
            }
            builder.append(model.getName());
        }
        return builder.toString();
    }

    private int getTextColor() {
        if (selectedChildren == null || selectedChildren.size() == 0) {
            return R.color.lightish_blue;
        } else {
            return R.color.dark_grey_blue;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  INIT TOOLBAR ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void initToolbar() {
        view.initToolbar(R.drawable.ic_arrow_back_white_24dp,
                onNavigationClickListener(),
                this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (isAdding) return false;
        if (selectedChildren == null || selectedChildren.size() == 0) {
            view.setPersons(getTextFromChild(), R.color.error_color);
            return false;
        }
        if (currentActivity == null) {
            view.showActivityError();
            return false;
        }
        isAdding = true;
        view.showLoading(true);
        subscriptions.add(dataService.addPost(groupId, currentActivity,
                selectedChildren, view.getContent(), photos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addedPost -> {
                    view.showLoading(true);
                    isAdding = false;
                    if (fileForDelete != null && fileForDelete.exists()) {
                        fileForDelete.delete();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(KEY_ADDED_POST, addedPost);
                    activitySwitcher.setResultAndGoBack(intent);
                }, throwable -> {
                    view.showLoading(false);
                    isAdding = false;
                    throwable.printStackTrace();
                }));
        return false;
    }

    private View.OnClickListener onNavigationClickListener() {
        return v -> activitySwitcher.open(new MainActivity.Screen());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   PHOTOS  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void addPhoto() {
        PhotoRotator.rotatePhotoFile(appContext, currentPhotoPath);
        String photo = currentPhotoPath.getPath();
        photos.add(photo);
        view.addPhoto(currentPhotoPath, v -> {
            view.removePhoto(v);
            photos.remove(photo);
            view.setPhotoButtonEnabled(photos.size() < MAX_COUNT_PHOTOS);
        });
        view.setPhotoButtonEnabled(photos.size() < MAX_COUNT_PHOTOS);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   ACTIVITIES  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void OnItemClickListener(int position, ChildActivity item) {
        currentActivity = item;
        view.setCurrentChildActivity(item);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  PERMISSIONS  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void camera() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                this::checkCameraPermission);
    }

    private void checkCameraPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
                Manifest.permission.CAMERA,
                this::openCamera);
    }

    private void openCamera() {
        fileForDelete = FilePathUtil.getCacheDir(application);
        currentPhotoPath = applicationSwitcher.openCamera(fileForDelete);
    }

    @Override
    public void gallery() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                applicationSwitcher::openGallery
        );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   LIFECICLE   ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }


}
