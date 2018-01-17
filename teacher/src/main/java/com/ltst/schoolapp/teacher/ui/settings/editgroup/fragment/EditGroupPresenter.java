package com.ltst.schoolapp.teacher.ui.settings.editgroup.fragment;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.core.util.PhotoRotator;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EditGroupPresenter implements EditGroupContract.Presenter {

    private final EditGroupContract.View view;
    private final ActivityScreenSwitcher screenSwitcher;
    private final DataService dataService;
    private final GalleryPictureLoader galleryPictureLoader;
    private final TeacherApplication context;
    private final PermissionsHandler permissionsHandler;
    private final ApplicationSwitcher applicationSwitcher;

    private CompositeSubscription subscriptions;
    private Uri photoPath;
    private File file;
    private long groupId;

    @Inject
    public EditGroupPresenter(EditGroupContract.View view,
                              ActivityScreenSwitcher screenSwitcher,
                              DataService dataService,
                              GalleryPictureLoader galleryPictureLoader,
                              TeacherApplication context,
                              PermissionsHandler permissionsHandler,
                              ApplicationSwitcher applicationSwitcher,
                              long groupId) {
        this.view = view;
        this.screenSwitcher = screenSwitcher;
        this.dataService = dataService;
        this.galleryPictureLoader = galleryPictureLoader;
        this.context = context;
        this.permissionsHandler = permissionsHandler;
        this.applicationSwitcher = applicationSwitcher;
        this.groupId = groupId;
    }


    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        getGroup();
    }

    private static final String SIS_GROUP_ID = "EditGroupPresenter.GroupId";

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
        long aLong = savedInstanceState.getLong(SIS_GROUP_ID);
        if (aLong != 0) {
            groupId = aLong;
        }

    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        outState.putLong(SIS_GROUP_ID, groupId);
    }

    @Override
    public void start() {
        if (subscriptions.isUnsubscribed()) subscriptions = new CompositeSubscription();
        initToolbar();
    }

    private void initToolbar() {
        view.setToolbarNavigationIcon(R.drawable.ic_clear_white_24dp, v -> {
            goBack();
        });
    }

    private void getGroup() {
        view.showLoading();
        subscriptions.add(dataService.getCachedGroupById(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::bindData, throwable -> {

                }));
    }

    public File getFile() {
        file = FilePathUtil.getCacheDir(context);
        photoPath = Uri.fromFile(file);
        return file;
    }

//    @NonNull
//    private File getFileWithRotate() {
//        StringBuilder pathBuilder = new StringBuilder();
//        pathBuilder.append(Environment.getExternalStorageDirectory().getPath())
//                .append("/")
//                .append(BuildConfig.APPLICATION_ID + "/avatars");
//        File directory = new File(pathBuilder.toString());
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        pathBuilder.append("/pic")
//                .append(System.currentTimeMillis())
//                .append(".png");
//        return new File(pathBuilder.toString());
//    }

    @Override
    public void photoFromCamera() {
        if (photoPath != null) {
            PhotoRotator.rotatePhotoFile(context, photoPath);
            view.setPhoto(photoPath);
        }
    }


    @Override
    public void photoFromGallery(Uri data) {
        try {
            Bitmap bitmap = galleryPictureLoader.getBitmap(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            File file = getFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            photoPath = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.setPhoto(photoPath);
    }

    @Override public void checkWriteExternalPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                view::showPhotoWay);
    }


    @Override public void checkCameraPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
                Manifest.permission.CAMERA,
                this::openCamera);
    }

    @Override public void openGallery() {
        applicationSwitcher.openGallery();
    }

    @Override public void openCamera() {
        applicationSwitcher.openCamera(getFile());
    }


    public void goBack() {
        screenSwitcher.goBack();
    }

    @Override
    public void validateAndUpdate(String title) {
        view.showLoading();
        String filePath = photoPath != null ? photoPath.getPath() : null;
        subscriptions.add(dataService.updateGroup(groupId, title, filePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(group -> {
                    view.showContent();
                    screenSwitcher.goBack();
                }, throwable -> {
                    if (throwable instanceof NetErrorException) {
                        view.showNetworkError();
                    }
                    throwable.printStackTrace();
                }));

    }


    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }
}
