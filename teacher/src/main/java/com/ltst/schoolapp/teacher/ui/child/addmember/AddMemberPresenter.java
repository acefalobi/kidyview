package com.ltst.schoolapp.teacher.ui.child.addmember;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.core.util.IntentsUtil;
import com.ltst.core.util.PhotoRotator;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.child.checkemail.CheckEmailFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//import com.tbruyelle.rxpermissions.RxPermissions;

public class AddMemberPresenter implements AddMemberContract.Presenter {

    private final AddMemberContract.View view;
    private final Member member;
    private final Child child;
    private final ActivityProvider activityProvider;
    private final GalleryPictureLoader galleryPictureLoader;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final TeacherApplication appContext;
    private final PermissionsHandler permissionsHandler;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ApplicationSwitcher applicationSwitcher;
    private Uri photoPath;
    private ActivityProvider.ActivityResultListener activityResultListener;
    private Subscription createMemberSubscription;
    private @AddMemberScope.AddMEmberScreenMode int screenMode;

    @Inject
    public AddMemberPresenter(AddMemberContract.View view,
                              Child child,
                              ActivityProvider activityProvider,
                              GalleryPictureLoader galleryPictureLoader,
                              DataService dataService,
                              ActivityScreenSwitcher activitySwitcher,
                              TeacherApplication appContext,
                              PermissionsHandler permissionsHandler,
                              Member member,
                              FragmentScreenSwitcher fragmentSwitcher,
                              ApplicationSwitcher applicationSwitcher,
                              int screenMode) {
        this.view = view;
        this.child = child;
        this.activityProvider = activityProvider;
        this.galleryPictureLoader = galleryPictureLoader;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.appContext = appContext;
        this.permissionsHandler = permissionsHandler;
        this.member = member;
        this.fragmentSwitcher = fragmentSwitcher;
        this.applicationSwitcher = applicationSwitcher;
        this.screenMode = screenMode;
        setupActivityResultListener();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        if (createMemberSubscription != null) {
            createMemberSubscription.unsubscribe();
            createMemberSubscription = null;
        }
    }

    @Override
    public void firstStart() {
        if (screenMode == AddMemberScope.SCREEN_MODE_EXIST) {
            view.bindExistMember(member);
        }
        else {
            view.bindNewMember(member);
        }
    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    private void setupActivityResultListener() {
        activityResultListener = (requestCode, resultCode, data) -> {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
                PhotoRotator.rotatePhotoFile(appContext, photoPath);
                view.setPhoto(photoPath);
            } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
                photoFromGallery(data.getData());
            }
        };
        activityProvider.setActivityResultListener(activityResultListener);
    }

    private void photoFromGallery(Uri data) {
        try {
            Bitmap bitmap = galleryPictureLoader.getBitmap(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            File file = FilePathUtil.getCacheDir(appContext);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            photoPath = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.setPhoto(photoPath);
    }

    @Override
    public void setStatus(String status) {
        member.setPosition(status);
    }

    @Override
    public void setName(String name) {
        member.setFirstName(name);
    }

    @Override
    public void setLastName(String lastName) {
        member.setLastName(lastName);
    }

    @Override
    public void setPhone(String phone) {
        member.setPhone(phone);
    }

    @Override
    public void setSecondPhone(String phone) {
        member.setSecondPhone(phone);
    }

    @Override
    public void setEmail(String email) {
        member.setEmail(email);
    }

    @Override
    public void done() {
        Map<ValidateType, String> needValidate = new HashMap<>();
        needValidate.put(ValidateType.STATUS, member.getPosition());
        needValidate.put(ValidateType.PERSONAL_EMAIL, member.getEmail());
        if (screenMode == AddMemberScope.SCREEN_MODE_CREATE) {
            needValidate.put(ValidateType.NAME, member.getFirstName());
            needValidate.put(ValidateType.LAST_NAME, member.getLastName());
            needValidate.put(ValidateType.PERSONAL_PHONE, member.getPhone());
            String secondPhone = member.getSecondPhone();
            if (!StringUtils.isBlank(secondPhone)) {
                needValidate.put(ValidateType.SECOND_PHONE, secondPhone);
            }
        }
        FieldsValidator.validate(needValidate)
                .subscribe(validateTypeStringMap -> {
                    addFamilyMember(member);
                }, throwable -> {
                    if (throwable instanceof ValidationThrowable) {
                        Set<ValidateType> validateTypes = ((ValidationThrowable) throwable).keySet();
                        if (validateTypes.contains(ValidateType.STATUS)) {
                            view.statusError();
                        } else if (validateTypes.contains(ValidateType.NAME)) {
                            view.nameError();
                        } else if (validateTypes.contains(ValidateType.LAST_NAME)) {
                            view.lastNameError();
                        } else if (validateTypes.contains(ValidateType.PERSONAL_PHONE)) {
                            view.phoneError();
                        } else if (validateTypes.contains(ValidateType.SECOND_PHONE)) {
                            view.secondPhoneError();
                        } else if (validateTypes.contains(ValidateType.PERSONAL_EMAIL)) {
                            view.emailError();
                        }
                    }
                });
    }

    @Override
    public void checkWriteStoragePermission() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                view::chosePhotoWay);
    }

    @Override
    public void checkPermissionAndOpenCamera() {
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
                Manifest.permission.CAMERA,
                this::openCamera);
    }

    @Override
    public void goBack() {
        activitySwitcher.goBack();
    }

    @Override
    public void openGallery() {
        applicationSwitcher.openGallery();
    }

    private void openCamera() {
        File file = FilePathUtil.getCacheDir(appContext);
        photoPath = Uri.fromFile(file);
        applicationSwitcher.openCamera(file);

    }

    private void addFamilyMember(Member member) {
        view.startLoad();
        if (photoPath != null) {
            member.setAvatarUrl(photoPath.getPath());
        }
        createMemberSubscription = dataService.inviteMember(child, member, screenMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(member1 -> {
                    child.addInvite(member1);
                    view.stopLoad();
                    fragmentSwitcher.openWithClearStack(CheckEmailFragment.Screen.FRAGMENT_TAG);
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.netError();
                    }
//
                });
    }
}
