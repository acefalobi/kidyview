package com.ltst.schoolapp.parent.ui.family.add.request;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
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
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.family.add.AddMemberActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RequestPresenter implements RequestContract.Presenter {

    private final RequestContract.View view;
    private final Member member;
    //    private final ArrayList<Long> childIds;
    private final long childId;
    private final long schoolId;
    private final ActivityProvider activityProvider;
    private final GalleryPictureLoader galleryPictureLoader;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final ParentApplication appContext;
    private final PermissionsHandler permissionsHandler;
    private final ApplicationSwitcher applicationSwitcher;
    private Uri photoPath;
    private ActivityProvider.ActivityResultListener activityResultListener;
    private Subscription createMemberSubscription;

    public static final int MEMBER_EXIST = 0;
    public static final int NEW_MEMBER = 1;

    @IntDef({MEMBER_EXIST, NEW_MEMBER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenStatus {
    }

    @ScreenStatus int screenStatus;


    @Inject
    public RequestPresenter(RequestContract.View view, Bundle activityParams,
                            ActivityProvider activityProvider,
                            GalleryPictureLoader galleryPictureLoader,
                            DataService dataService,
                            ActivityScreenSwitcher activitySwitcher,
                            ParentApplication appContext,
                            PermissionsHandler permissionsHandler,
                            Member member,
                            ApplicationSwitcher applicationSwitcher,
                            int screenStatus) {
        this.view = view;
        this.activityProvider = activityProvider;
        this.galleryPictureLoader = galleryPictureLoader;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.appContext = appContext;
        this.permissionsHandler = permissionsHandler;
        this.member = member;
        this.childId = activityParams.getLong(AddMemberActivity.Screen.CHILD_ID_KEY);
        this.schoolId = activityParams.getLong(AddMemberActivity.Screen.SCHOOL_ID_KEY);
        this.applicationSwitcher = applicationSwitcher;
        setupActivityResultListener();
        this.screenStatus = screenStatus;

    }

    @Override
    public void start() {
        if (createMemberSubscription != null) {
            createMemberSubscription.unsubscribe();
            createMemberSubscription = null;
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void firstStart() {
        if (screenStatus == MEMBER_EXIST) {
            view.bindFindedMmeber(member);
        } else {
            view.bindNewMember(member.getEmail());
        }
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
    public void done() {
        Map<ValidateType, String> needValidate = new HashMap<>();
        if (screenStatus == NEW_MEMBER) {
            needValidate.put(ValidateType.STATUS, member.getPosition());
            needValidate.put(ValidateType.NAME, member.getFirstName());
            needValidate.put(ValidateType.LAST_NAME, member.getLastName());
            needValidate.put(ValidateType.PERSONAL_PHONE, member.getPhone());
            needValidate.put(ValidateType.PERSONAL_EMAIL, member.getEmail());
            String secondPhone = member.getSecondPhone();
            if (!StringUtils.isBlank(secondPhone)) {
                needValidate.put(ValidateType.SECOND_PHONE, secondPhone);
            }
        } else {
            needValidate.put(ValidateType.STATUS, member.getPosition());
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
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
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
        createMemberSubscription = dataService.inviteMember(childId, schoolId, member, screenStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(member1 -> {
//                    child.addInvite(member1);
                    view.stopLoad();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(AddMemberActivity.Screen.RESULT_FAMILY_MEMBER_KEY, member1);
                    activitySwitcher.setResultAndFinish(resultIntent);
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.netError();
                    }
//
                });
    }

    @Override
    public void goBack() {
        activitySwitcher.goBack();
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
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }
}
